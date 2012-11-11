package at.ac.tuwien.infosys.dslab.server.auction;

import at.ac.tuwien.infosys.dslab.server.auth.Duration;

import java.util.HashMap;
import java.util.Map;

public abstract class AuctionUser {

    private final String name;
    private final Map<Integer, Auction> warehouse;

    public AuctionUser(String name, Map<Integer, Auction> warehouse) {
        this.name = name;
        this.warehouse = warehouse;

    }

    @Override
    public final String toString() {
        return this.name;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof AuctionUser)) return false;

        AuctionUser that = (AuctionUser) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public final int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public int createAuction(Duration duration, String description) {
            Auction auction = AuctionFactory.createAuction(this, description, duration.getEnd());
            this.warehouse.put(auction.hashCode(), auction);
            return auction.hashCode();
    }

    public void bid(int auctionKey, double amount) {
            Auction auction = warehouse.get(auctionKey);
            auction.bid(new Bid(this, amount));
    }

    static AuctionUser getNullAuctionUser() {
        return new AuctionUser("none", new HashMap<Integer, Auction>()) {
            @Override
            public void notifyOverbid(String description) {
            }
            @Override
            public void notifyAuctionEnd(double amount, AuctionUser winner, String description) {
            }
        };
    }

    public void logout() {
        throw new UnsupportedOperationException();
    }

    public abstract void notifyOverbid(String description);

    public abstract void notifyAuctionEnd(double amount, AuctionUser winner, String description);
}
