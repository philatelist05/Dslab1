package at.ac.tuwien.infosys.dslab.server;

import at.ac.tuwien.infosys.dslab.server.auction.Auction;
import at.ac.tuwien.infosys.dslab.server.auth.Authenticator;
import at.ac.tuwien.infosys.dslab.common.network.TCPServer;
import at.ac.tuwien.infosys.dslab.common.network.TCPServerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Main {

    public static void main(String[] args) {
        int serverPort = parseArgs(args);

        ReadWriteLock lock = new ReentrantReadWriteLock();
        ConcurrentHashMap<Integer, Auction> auctionWarehouse = new ConcurrentHashMap<Integer, Auction>();
        Authenticator authenticator = new Authenticator(Arrays.asList("Stefan", "Max", "Rainer"), lock);

        TCPServer server = new TCPServer(new TCPServerFactory(serverPort));
        server.addObserver(new ServerHandler(authenticator, auctionWarehouse, lock));
        server.waitForClients();

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(System.getProperty("line.separator"));
        while (scanner.hasNext()) {
            String line = scanner.next().trim();
            if (line.equals("!end")) {
                close(auctionWarehouse, server);
                break;
            } else {
                System.out.println("unknown command!");
                System.out.println("supported commands: !end");
            }
        }
    }

    private static void close(Map<Integer, Auction> auctionWarehouse, TCPServer server) {
        try {
            server.close();
            for (Auction auction:auctionWarehouse.values()) {
                auction.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int parseArgs(String[] args) {
        if (args.length != 1) {
            usage();
        }
        return parsePortNumber(args[0]);
    }

    private static int parsePortNumber(String arg) {
        try {
            Integer port = Integer.valueOf(arg);
            Integer lowerRange = 0;
            Integer upperRange = 65535;
            if (port.compareTo(lowerRange) <= 0 || port.compareTo(upperRange) >= 0) {
                usage();
            }
            return port;
        } catch (NumberFormatException e) {
            usage();
        }

        throw new AssertionError();
    }

    private static void usage() {
        System.err.println("Usage: Server <listenPort>");
        System.exit(-1);
    }
}
