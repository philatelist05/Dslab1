package at.ac.tuwien.infosys.dslab.server;

import at.ac.tuwien.infosys.dslab.common.network.UDPClient;
import at.ac.tuwien.infosys.dslab.server.auction.Auction;
import at.ac.tuwien.infosys.dslab.server.auction.AuctionUser;
import at.ac.tuwien.infosys.dslab.server.auction.UserFactory;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public class NetworkUserFactory implements UserFactory {

    private final UDPClient client;
    private final Map<Integer, Auction> warehouse;
    private final ReadWriteLock readWriteLock;

    public NetworkUserFactory(UDPClient client, Map<Integer, Auction> warehouse, ReadWriteLock readWriteLock) {
        this.client = client;
        this.warehouse = warehouse;
        this.readWriteLock = readWriteLock;
    }

    @Override
    public AuctionUser createUser(String userName) {
        return new NetworkUser(userName, client, warehouse, readWriteLock);
    }
}
