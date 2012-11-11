package at.ac.tuwien.infosys.dslab.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    private static class ProgramArguments {
        private final InetAddress auctionServerAddress;
        private final int auctionServerTcpPort;
        private final int udpPort;

        private ProgramArguments(String auctionServerHostName, int auctionServerTcpPort, int udpPort) throws UnknownHostException {
            this.auctionServerAddress = InetAddress.getByName(auctionServerHostName);
            this.auctionServerTcpPort = auctionServerTcpPort;
            this.udpPort = udpPort;
        }
    }

    public static void main(String[] args) {
        ProgramArguments arguments = parseArgs(args);

        try {
            ClientHandler handler = new ClientHandler(arguments.udpPort, arguments.auctionServerAddress, arguments.auctionServerTcpPort);
            handler.handle(System.in, System.out);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private static ProgramArguments parseArgs(String[] args) {
        if (args.length != 3)
            usage();
        String host = args[0];

        try {
            int tcpPort = parsePort(args[1]);
            int udpPort = parsePort(args[2]);
            return new ProgramArguments(host, tcpPort, udpPort);
        } catch (UnknownHostException e) {
            System.err.println("Can't resolve Hostname " + host);
            System.exit(-1);
        }
        throw new AssertionError();
    }

    private static int parsePort(String arg) {
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
        System.err.println("Usage: Client <hostName> <tcpPort> <udpPort>");
        System.exit(-1);
    }
}
