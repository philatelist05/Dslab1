package at.ac.tuwien.infosys.dslab.server.auction;

public interface UserFactory {
    /**
     * Creates an AuctionUser for the specified name.
     *
     * @param userName the UserName
     * @return an AuctionUser for the specified name
     */
    public AuctionUser createUser(String userName);
}
