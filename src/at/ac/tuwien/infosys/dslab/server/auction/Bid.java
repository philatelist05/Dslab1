package at.ac.tuwien.infosys.dslab.server.auction;

public class Bid implements Comparable<Bid> {

    private final double amount;
    private final AuctionUser bidder;

    public Bid(AuctionUser bidder, double amount) {
        this.amount = amount;
        this.bidder = bidder;
    }

    @Override
    public int compareTo(Bid bid) {
        return new Double(this.amount).compareTo(bid.amount);
    }

    double getAmount() {
        return amount;
    }

    AuctionUser getBidder() {
        return bidder;
    }
}
