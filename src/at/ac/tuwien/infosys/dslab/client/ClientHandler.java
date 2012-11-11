package at.ac.tuwien.infosys.dslab.client;

import at.ac.tuwien.infosys.dslab.common.network.TCPClient;
import at.ac.tuwien.infosys.dslab.common.network.TCPSession;
import at.ac.tuwien.infosys.dslab.common.network.UDPServer;

import javax.net.SocketFactory;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler {

    private final TCPClient tcpClient;
    private final TCPSession tcpSession;
    private final int udpListenPort;

    public ClientHandler(int udpListenPort, InetAddress serverAddress, int serverPort) {
        this.tcpClient = new TCPClient(SocketFactory.getDefault());
        this.tcpSession = this.tcpClient.connect(serverAddress, serverPort);
        this.udpListenPort = udpListenPort;
    }

    public void handle(InputStream in, PrintStream out) {
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter(System.getProperty("line.separator"));
        UDPServer udpServer = null;

        while (scanner.hasNext()) {
            Matcher matcher;
            String line = scanner.next().trim();
            //!end
            if (line.equals("!end")) {
                this.tcpSession.writeString("!endSession");
                this.tcpClient.close();
                break;
            }
            //!logout
            else if (line.equals("!logout")) {
                if (udpServer != null) {
                    udpServer.close();
                }
                String response = executeCommand(line);
                out.println(response);
            }
            //!login <username>
            else if ((matcher = Pattern.compile("^\\!login\\s+(\\w+)$").matcher(line)).matches()) {
                String username = matcher.group(1);

                String response = executeCommand("!login " + username + " " + this.udpListenPort);
                out.println(response);
                if (response.trim().equals("Successfully logged in")) {
                    executeLoginCommand(scanner, out, username);
                }

            } else {
                String response = executeCommand(line);
                out.println(response);
            }
        }
        scanner.close();
        out.close();
    }

    private String executeCommand(String cmd) {
        this.tcpSession.writeString(cmd);
        return this.tcpSession.readString();
    }

    private void executeLoginCommand(Scanner scanner, PrintStream out, String currentUser) {
        Matcher matcher;
        boolean loggedIn = true;
        UDPServer udpServer = createUDPServer(currentUser);
        udpServer.waitForClients();

        while (loggedIn && scanner.hasNext()) {
            String userCommandText = scanner.next().trim();
            //!logout
            if (userCommandText.equals("!logout")) {
                String response = executeCommand(userCommandText);
                out.println(response);
                loggedIn = false;
            }
            //!login <username>
            else if ((matcher = Pattern.compile("^\\!login\\s+(\\w+)$").matcher(userCommandText)).matches()) {
                String response = executeCommand("!login " + currentUser + " " + this.udpListenPort);
                out.println(response);
            } else {
                String response = executeCommand(userCommandText);
                out.println(response);
            }
        }
        udpServer.close();
    }

    private UDPServer createUDPServer(String currentUser) {
        UDPServer udpServer = new UDPServer(this.udpListenPort);
        udpServer.addObserver(new UDPNotificationImpl(currentUser));
        return udpServer;
    }

}
