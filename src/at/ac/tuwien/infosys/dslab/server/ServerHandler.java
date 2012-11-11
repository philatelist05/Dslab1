package at.ac.tuwien.infosys.dslab.server;

import at.ac.tuwien.infosys.dslab.server.auction.*;
import at.ac.tuwien.infosys.dslab.server.auth.*;
import at.ac.tuwien.infosys.dslab.common.network.TCPSession;
import at.ac.tuwien.infosys.dslab.common.network.UDPClient;
import at.ac.tuwien.infosys.dslab.common.observer.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerHandler implements Observer<TCPSession> {

    private final Authenticator authenticator;
    private final Map<Integer, Auction> warehouse;
    private final ReadWriteLock readWriteLock;

    public ServerHandler(Authenticator authenticator, Map<Integer, Auction> warehouse, ReadWriteLock readWriteLock) {
        this.authenticator = authenticator;
        this.warehouse = warehouse;
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void update(Observable observable, TCPSession session) {
        Matcher matcher;
        boolean connected = true;

        while (connected) {
            String commandText = session.readString().trim();

            if (commandText.equals("!endSession")) {
                connected = false;
            }
            //!list
            else if (commandText.equals("!list")) {
                executeListCommand(session);
            }
            //!logout (not allowed here)
            else if (commandText.equals("!logout")) {
                session.writeString("You have to log in first");
            }
            //!login <username> <udpPort>
            else if ((matcher = Pattern.compile("^\\!login\\s+(\\w+)\\s+(\\d+)$").matcher(commandText)).matches()) {
                String username = matcher.group(1);
                int udpPort = Integer.parseInt(matcher.group(2));

                executeLoginCommand(session, username, udpPort);
            } else {
                session.writeString("Unknown Command '" + commandText + "'");
            }
        }
        session.close();
    }

    private void executeLoginCommand(TCPSession session, String username, int udpPort) {
        Matcher matcher;

        UDPClient client = new UDPClient(session.getRemoteAddress(), udpPort);
        UserFactory userFactory = new NetworkUserFactory(client, this.warehouse, this.readWriteLock);
        try {
            AuctionUser user = this.authenticator.login(username, userFactory);
            boolean loggedIn = true;
            session.writeString("Successfully logged in");

            while (loggedIn) {
                String userCommandText = session.readString().trim();
                //!list
                if (userCommandText.equals("!list")) {
                    executeListCommand(session);
                }
                //!create <duration> <description>
                else if ((matcher = Pattern.compile("^\\!create\\s+(\\d+)\\s+(\\S.*)$").matcher(userCommandText)).matches()) {
                    int durationInSeconds = Integer.parseInt(matcher.group(1));
                    String description = matcher.group(2);

                    executeCreateAuctionCommand(session, user, durationInSeconds, description);
                }
                //!bid <auction-id> <amount>
                else if ((matcher = Pattern.compile("^\\!bid\\s+(\\d+)\\s+(\\d+|\\d+\\.\\d+)$").matcher(userCommandText)).matches()) {
                    int auctionId = Integer.parseInt(matcher.group(1));
                    double amount = Double.parseDouble(matcher.group(2));
                    executeBidCommand(session, user, auctionId, amount);
                }
                //!logout
                else if (userCommandText.equals("!logout")) {
                    executeLogoutCommand(session, user);
                    loggedIn = false;
                }
                //!login <username> <udpPort>
                else if ((matcher = Pattern.compile("^\\!login\\s+(\\w+)\\s+(\\d+)$").matcher(userCommandText)).matches()) {
                    session.writeString("You are already logged in");
                } //!logout
                else if (userCommandText.equals("!end")) {
                    session.writeString("You have to logout first");
                }
                else {
                    session.writeString("Unknown Command '" + userCommandText + "'");
                }
            }

        } catch (AuthenticationException e) {
            session.writeString(e.getMessage());
        }
    }

    private void executeCreateAuctionCommand(TCPSession session, AuctionUser user, int durationInSeconds, String description) {
        Duration duration = Duration.createFromNow(durationInSeconds);
        int auctionId = user.createAuction(duration, description);

        StringBuilder builder = new StringBuilder();
        builder.append("An auction '");
        builder.append(description);
        builder.append("' with id ");
        builder.append(auctionId);
        builder.append(" has been created and will end on ");
        builder.append(duration.getEnd());
        session.writeString(builder.toString());
    }

    private void executeLogoutCommand(TCPSession session, AuctionUser user) {
        this.authenticator.logout(user);
        session.writeString("Successfully logged out");
    }

    private void executeBidCommand(TCPSession session, AuctionUser user, int auctionId, double amount) {
        StringBuilder builder = new StringBuilder();
        try {
            user.bid(auctionId, amount);

            builder.append("You successfully bid with ");
            builder.append(amount);
            builder.append(" on auction with id ");
            builder.append(auctionId);
        } catch (IllegalStateException e) {
            session.writeString(e.getMessage());
        } catch (NoSuchElementException e) {
            session.writeString(e.getMessage());
        }
        session.writeString(builder.toString());
    }

    private void executeListCommand(TCPSession session) {
        StringBuilder builder = new StringBuilder();
        Collection<Auction> openAuctions = this.warehouse.values();

        if (openAuctions.size() <= 0) {
            builder.append("0.00 none");
        } else {
            for (Auction auction : openAuctions) {
                builder.append(auction.toString());
               builder.append(System.getProperty("line.separator"));
            }
        }
        session.writeString(builder.toString());
    }
}
