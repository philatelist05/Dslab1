package at.ac.tuwien.infosys.dslab.server.auction;

import java.io.IOException;
import java.util.*;

class AuctionImpl implements Auction {

    private static int numAuctions = 0;
    private final int id;
    private final Date endDate;
    private final AuctionUser owner;
    private final String description;
    private final List<Bid> bids;
    private boolean isOpen;
    private Map<Integer, Auction> warehouse;

    AuctionImpl(AuctionUser owner, String description, Date endDate) {
        if (endDate.before(new Date()))
            throw new IllegalArgumentException("The specified date must be in the future!");

        this.endDate = new Date(endDate.getTime());
        this.id = ++AuctionImpl.numAuctions;
        this.description = description;
        this.owner = owner;
        this.bids = new ArrayList<Bid>();
        this.isOpen = true;
    }

    @Override
    public final String toString() {
        return this.id + " '" + this.description + "' " + this.endDate.toString() + " '" + this.owner.toString() + "'";
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuctionImpl auction = (AuctionImpl) o;

        return id == auction.id;

    }

    @Override
    public final int hashCode() {
        return id;
    }

    @Override
    public void bid(Bid bid) {
        if (!this.isOpen)
            throw new IllegalStateException("You can't bid on a closed auction!");

        if (this.bids.size() > 0) {
            Bid highestBid = getHighestBid();

            if (bid.compareTo(highestBid) <= 0)
                throw new IllegalStateException("Bid unsuccessful because there's a higher bid!");

            highestBid.getBidder().notifyOverbid(this.description);
        }

        this.bids.add(bid);
    }

    Bid getHighestBid() {
        return Collections.max(bids);
    }

    void endNow() {
        if (this.bids.size() > 0) {
            Bid highestBid = getHighestBid();
            notifyAuctionEnd(highestBid);
        } else {
            owner.notifyAuctionEnd(0, AuctionUser.getNullAuctionUser(), "");
        }

        if (this.warehouse != null)
            this.warehouse.remove(this.hashCode());
        this.isOpen = false;
    }

    private void notifyAuctionEnd(Bid highestBid) {
        double amount = highestBid.getAmount();
        AuctionUser bidder = highestBid.getBidder();
        highestBid.getBidder().notifyAuctionEnd(amount, bidder, this.description);
        this.owner.notifyAuctionEnd(amount, bidder, this.description);
    }

    Date getEndDate() {
        return new Date(this.endDate.getTime());
    }

    @Override
    public void assignWarehouse(Map<Integer, Auction> warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public void close() throws IOException {
    }
}
