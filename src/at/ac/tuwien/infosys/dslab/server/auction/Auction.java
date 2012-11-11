package at.ac.tuwien.infosys.dslab.server.auction;

import java.io.Closeable;
import java.util.Map;

public interface Auction extends Closeable {
    /**
     * Bids on this Auction.
     *
     * @param bid the Bid
     */
    void bid(Bid bid);

    /**
     * Assigns a Warehouse to the Auction. This is necessary to remove the Auction when  it ends.
     *
     * @param warehouse The "parent"-Warehouse.
     */
    void assignWarehouse(Map<Integer, Auction> warehouse);

    /**
     * Removes the first occurrence of the specified element from this Warehouse.
     * If this list does not contain the element, it is unchanged.
     * More formally, removes the element with the lowest index i such that (o==null ? get(i)==null : o.equals(get(i))) (if such an element exists).
     * Returns true if this list contained the specified element (or equivalently, if this list changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return if this list contained the specified element
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this Auction.
     *
     * @return the hash code value for this Auction
     */
    int hashCode();

    /**
     * Returns a String representation of this Auction. The output should of the following format:
     * <p/>
     * 'Description' Owner EndDate HighestBidAmount HighestBidder
     * 'Apple I' wozniak 10.10.2012 21:00 CET 10000.00 gates
     *
     * @return String representation of this Auction(@see above)
     */
    String toString();
}
