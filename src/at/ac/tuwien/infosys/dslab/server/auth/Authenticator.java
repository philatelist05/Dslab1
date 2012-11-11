package at.ac.tuwien.infosys.dslab.server.auth;

import at.ac.tuwien.infosys.dslab.server.auction.AuctionUser;
import at.ac.tuwien.infosys.dslab.server.auction.UserFactory;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;

public final class Authenticator {

    private final Set<String> users;
    private final HashSet<AuctionUser> authenticatedUsers;
    private final ReadWriteLock readWriteLock;

    public Authenticator(List<String> userList, ReadWriteLock readWriteLock) {
        this.users = new HashSet<String>(userList);
        this.authenticatedUsers = new HashSet<AuctionUser>();
        this.readWriteLock = readWriteLock;
    }

    public AuctionUser login(String userName, UserFactory userFactory) {
        try {
            this.readWriteLock.writeLock().lock();
            if (!this.users.contains(userName)) {
                throw new AuthenticationException("User '" + userName + "' doesn't exist");
            }
            AuctionUser user = userFactory.createUser(userName);
            if (this.authenticatedUsers.contains(user)) {
                throw new AuthenticationException("You're already logged in on another Client");
            }
            this.authenticatedUsers.add(user);
            return user;
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    public void logout(AuctionUser user) {
        try {
            this.readWriteLock.writeLock().lock();
            user.logout();
            this.authenticatedUsers.remove(user);
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }
}
