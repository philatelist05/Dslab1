package at.ac.tuwien.infosys.dslab.server;

import at.ac.tuwien.infosys.dslab.common.network.UDPClient;
import at.ac.tuwien.infosys.dslab.server.auction.*;
import at.ac.tuwien.infosys.dslab.server.auth.Duration;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

class NetworkUser extends AuctionUser {

    private final UDPClient client;
    private final ReadWriteLock readWriteLock;

    NetworkUser(String name, UDPClient client, Map<Integer, Auction> warehouse, ReadWriteLock readWriteLock) {
        super(name, warehouse);
        this.client = client;
        this.readWriteLock = readWriteLock;
    }

    @Override
    public int createAuction(Duration duration, String description) {
        this.readWriteLock.writeLock().lock();
        try {
            return super.createAuction(duration, description);
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void bid(int auctionKey, double amount) {
        this.readWriteLock.writeLock().lock();
        try {
            super.bid(auctionKey, amount);
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void notifyOverbid(String description) {
        this.client.send("!new-bid " + description);
    }

    @Override
    public final void notifyAuctionEnd(double amount, AuctionUser winner, String description) {
        this.client.send("!auction-ended " + winner.toString() + " " + amount + " " + description);
    }

    @Override
    public void logout() {
        this.client.close();
    }
}
