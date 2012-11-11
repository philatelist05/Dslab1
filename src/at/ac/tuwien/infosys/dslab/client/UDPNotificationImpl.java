package at.ac.tuwien.infosys.dslab.client;

import at.ac.tuwien.infosys.dslab.common.network.UDPNotification;
import at.ac.tuwien.infosys.dslab.common.observer.Observable;
import at.ac.tuwien.infosys.dslab.common.observer.Observer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UDPNotificationImpl implements Observer<UDPNotification> {

    private final String currentUser;

    public UDPNotificationImpl(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void update(Observable observable, UDPNotification notification) {
        Matcher matcher;
        String message = notification.getReceivedMessage().trim();

        //!auction-ended dave 250.00 description
        if ((matcher = Pattern.compile("^\\!auction\\-ended\\s+(\\w+)\\s+(\\d+|\\d+\\.\\d+)\\s+(\\S.*)$").matcher(message)).matches()) {
            String winner = matcher.group(1);
            double amount = Double.parseDouble(matcher.group(2));
            String description = matcher.group(3);

            if (winner.equals(this.currentUser)) {
                System.out.println("The auction '" + description + "' has ended. You won with " + amount);
            } else {
                System.out.println("The auction '" + description + "' has ended. " + winner + " won with " + amount);
            }
        }
        //!new-bid notebook
        else if ((matcher = Pattern.compile("^\\!new\\-bid\\s+(\\S.*)$").matcher(message)).matches()) {
            String description = matcher.group(1);
            System.out.println("You have been overbid on '" + description + "'");
        }

    }
}
