package at.ac.tuwien.infosys.dslab.server.auction;

import java.util.Date;

public class AuctionFactory {
    public static Auction createAuction(AuctionUser owner, String description, Date endDate) {
        AuctionImpl auction = new AuctionImpl(owner, description, endDate);
        return new TimerAuctionImpl(auction);
    }
}
