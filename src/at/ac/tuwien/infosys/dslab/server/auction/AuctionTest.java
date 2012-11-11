package at.ac.tuwien.infosys.dslab.server.auction;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class AuctionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAuctionWithIllegalEndDate() {
        Date endDate = new Date(0);
        new AuctionImpl(new DummyAuctionUser(""), "Description", endDate);
    }

    @Test
    public void testHighestBid() {
        AuctionImpl auction = new AuctionImpl(new DummyAuctionUser(""), "Description", new Date());
        auction.bid(new Bid(new DummyAuctionUser(""), 100));
        Bid expectedBid = new Bid(new DummyAuctionUser(""), 200);
        auction.bid(expectedBid);

        Bid actualBid = auction.getHighestBid();
        assertEquals(expectedBid, actualBid);
    }

    @Test
    public void testBiddingNoInfluence() {
        AuctionImpl auction1 = new AuctionImpl(new DummyAuctionUser(""), "Description", new Date());
        AuctionImpl auction2 = new AuctionImpl(new DummyAuctionUser(""), "Description", new Date());

        Bid expectedBid = new Bid(new DummyAuctionUser(""), 200);
        auction1.bid(expectedBid);
        auction2.bid(new Bid(new DummyAuctionUser(""), 300));

        Bid actualBid = auction1.getHighestBid();
        assertEquals(expectedBid, actualBid);
    }

    @Test
    public void testNotifyHighestBidder() {
        AuctionImpl auction = new AuctionImpl(new DummyAuctionUser(""), "Description", new Date());
        DummyAuctionUser user1 = new DummyAuctionUser("");
        auction.bid(new Bid(user1, 100));
        DummyAuctionUser user2 = new DummyAuctionUser("");
        auction.bid(new Bid(user2, 200));

        assertTrue(user1.overbidden);
    }

    @Test
    public void testNoOverbidNotification() {
        AuctionImpl auction = new AuctionImpl(new DummyAuctionUser(""), "Description", new Date());
        DummyAuctionUser user = new DummyAuctionUser("");
        auction.bid(new Bid(user, 100));

        assertFalse(user.overbidden);
    }

    @Test
    public void testAuctionEndingWithBid() {
        DummyAuctionUser owner = new DummyAuctionUser("");
        AuctionImpl auction = new AuctionImpl(owner, "Description", new Date());
        DummyAuctionUser user1= new DummyAuctionUser("");
        auction.bid(new Bid(user1, 100));

        DummyAuctionUser user2 = new DummyAuctionUser("");
        auction.bid(new Bid(user2, 200));
        auction.endNow();

        assertTrue(user2.wins);
        assertEquals(200.0, user2.amount);
        assertFalse(user1.wins);
        assertFalse(user1.auctionEndForOwner);
        assertFalse(user2.auctionEndForOwner);
        assertTrue(owner.auctionEndForOwner);
    }

    @Test    (expected = IllegalStateException.class)
    public void testAuctionEndingWithUnsuccessfulBid() {
       // try {
            DummyAuctionUser owner = new DummyAuctionUser("");
            AuctionImpl auction = new AuctionImpl(owner, "Description", new Date());
            DummyAuctionUser user1 = new DummyAuctionUser("");
            auction.bid(new Bid(user1, 200));

            DummyAuctionUser user2 = new DummyAuctionUser("");
            auction.bid(new Bid(user2, 100));
            auction.endNow();

            assertFalse(user1.overbidden);
            assertFalse(user2.overbidden);
       // } catch (IllegalStateException e) {
       //     System.out.println("dcd");
       // }
    }

    @Test
    public void testAuctionEndingWithoutBids() {
        DummyAuctionUser owner = new DummyAuctionUser("");
        AuctionImpl auction = new AuctionImpl(owner, "Description", new Date());
        auction.endNow();

        assertTrue(owner.auctionEndForOwner);
    }

    @Test (expected = IllegalStateException.class)
    public void testUnsuccessfulBid() {
        AuctionImpl auction = new AuctionImpl(new DummyAuctionUser(""), "Description", new Date());
        auction.bid(new Bid(new DummyAuctionUser(""), 200));
        auction.bid(new Bid(new DummyAuctionUser(""), 100));
    }

    @Test
    public void testSuccessfulBid() {
        AuctionImpl auction = new AuctionImpl(new DummyAuctionUser(""), "Description", new Date());
        auction.bid(new Bid(new DummyAuctionUser(""), 100));
        auction.bid(new Bid(new DummyAuctionUser(""), 200));
    }

    @Test
    public void testSelfOverbidShouldFail() {

    }

    private class DummyAuctionUser extends AuctionUser {

        private boolean overbidden;
        private boolean wins;
        private double amount;
        private boolean auctionEndForOwner;

        public DummyAuctionUser(String name) {
            super(name, new HashMap<Integer, Auction>());
            this.overbidden = false;
            this.wins = false;
            this.amount = 0.0;
            this.auctionEndForOwner = false;
        }

        @Override
        public void notifyOverbid(String message) {
            this.overbidden = true;
        }

        @Override
        public void notifyAuctionEnd(double amount, AuctionUser winner, String description) {
            if (winner == this) {
                notifyWinner(amount, description);
            } else {
                notifyOwner();
            }
        }

        private void notifyWinner(double amount, String description) {
            this.wins = true;
            this.amount = amount;
        }

        private void notifyOwner() {
            this.auctionEndForOwner = true;
        }
    }

}
