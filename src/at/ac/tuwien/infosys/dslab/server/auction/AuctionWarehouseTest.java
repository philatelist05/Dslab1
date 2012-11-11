package at.ac.tuwien.infosys.dslab.server.auction;

import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.*;

public class AuctionWarehouseTest {

    @Test
    public void testOpenAuction() {
        Map<Integer, Auction> auctionWarehouse = new HashMap<Integer, Auction>();
        Date endDate = new Date(System.currentTimeMillis() + 10000);
        Auction auction = new AuctionImpl(new DummyAuctionUser("", auctionWarehouse), "Description", endDate);
        auctionWarehouse.put(auction.hashCode(),auction);
        Collection<Auction> actualAuctions = auctionWarehouse.values();

        Collection<Auction> expectedAuctions = new LinkedList<Auction>();
        expectedAuctions.add(auction);
        assertEquals(expectedAuctions, actualAuctions);
    }

    @Test
    public void testTwoAuctionOutput() {
        Map<Integer, Auction> auctionWarehouse = new HashMap<Integer, Auction>();
        Date currentDate = new Date();
        Auction auction = new AuctionImpl(new DummyAuctionUser("Owner1",auctionWarehouse), "Description1", currentDate);
        auctionWarehouse.put(auction.hashCode(),auction);
        auction = new AuctionImpl(new DummyAuctionUser("Owner2", auctionWarehouse), "Description2", currentDate);
        auctionWarehouse.put(auction.hashCode(),auction);
        Collection<Auction> actualAuctions = auctionWarehouse.values();
        assertEquals(2, actualAuctions.size());

        auction = actualAuctions.toArray(new Auction[0])[0];
        String actualAuctionString = auction.toString();
        String expectedAuctionString = auction.hashCode() + " 'Description1' " + currentDate.toString() + " 'Owner1'";
        assertEquals(expectedAuctionString, actualAuctionString);

        auction = actualAuctions.toArray(new Auction[0])[1];
        actualAuctionString = auction.toString();
        expectedAuctionString = auction.hashCode() + " 'Description2' " + currentDate.toString() + " 'Owner2'";
        assertEquals(expectedAuctionString, actualAuctionString);
    }

    @Test
    public void testAuctionOutput() {
        Map<Integer, Auction> auctionWarehouse = new HashMap<Integer, Auction>();
        Date endDate = new Date();
        AuctionImpl auction = new AuctionImpl(new DummyAuctionUser("Owner1", auctionWarehouse), "Description1", endDate);
        auctionWarehouse.put(auction.hashCode(),auction);
        Collection<Auction> actualAuctions = auctionWarehouse.values();
        assertEquals(1, actualAuctions.size());

        Auction auction1 = actualAuctions.toArray(new Auction[0])[0];
        String actualAuctionString = auction1.toString();
        String expectedAuctionString = auction1.hashCode() + " 'Description1' " + endDate.toString() + " 'Owner1'";
        assertEquals(expectedAuctionString, actualAuctionString);
    }

    @Test
    public void testRemoveAuctionAfterEnding() {
        Map<Integer, Auction> auctionWarehouse = new HashMap<Integer, Auction>();
        AuctionImpl auction = new AuctionImpl(new DummyAuctionUser("", auctionWarehouse), "Description", new Date());
        auctionWarehouse.put(auction.hashCode(),auction);
        assertEquals(1, auctionWarehouse.values().size());

        auction.endNow();
        assertEquals(0, auctionWarehouse.values().size());
    }

     private class DummyAuctionUser extends AuctionUser {

         public DummyAuctionUser(String name, Map<Integer, Auction> warehouse) {
             super(name, warehouse);
         }

         @Override
         public void notifyOverbid(String description) {
         }

         @Override
         public void notifyAuctionEnd(double amount, AuctionUser winner, String description) {
         }
     }
}
