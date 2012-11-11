package at.ac.tuwien.infosys.dslab.server.auction;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

class TimerAuctionImpl implements Auction {

    private final Timer timer;
    private final AuctionImpl auction;

    TimerAuctionImpl(AuctionImpl auction) {
        this.auction = auction;
        this.timer = new Timer("Timer thread");
        this.timer.schedule(new Task(), auction.getEndDate());
    }

    @Override
    public void close() throws IOException {
        auction.close();
        this.timer.cancel();
    }

    @Override
    public void bid(Bid bid) {
        auction.bid(bid);
    }

    @Override
    public void assignWarehouse(Map<Integer, Auction> warehouse) {
        auction.assignWarehouse(warehouse);
    }

    @Override
    public String toString() {
        return auction.toString();
    }

    @Override
    public boolean equals(Object o) {
        return auction.equals(o);
    }

    @Override
    public int hashCode() {
        return auction.hashCode();
    }

    private class Task extends TimerTask {
        @Override
        public void run() {
            TimerAuctionImpl.this.auction.endNow();
            //This guarantees that this Task is the last one
            TimerAuctionImpl.this.timer.cancel();
        }
    }
}
