package at.ac.tuwien.infosys.dslab.server.auth;

import at.ac.tuwien.infosys.dslab.server.auction.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.Assert.assertEquals;

public class AuthenticatorTest {

    @Test
    public void testNewAuthenticator() {
        new Authenticator(Arrays.asList("a", "a", "c"), new DummyReadWriteLock());
    }

    @Test (expected = AuthenticationException.class)
    public void testMultipleLogin()throws Exception{
        Authenticator authenticator = new Authenticator(Arrays.asList("a", "b", "c"), new DummyReadWriteLock());
        AuctionUser user1 = authenticator.login("a",new DummyAuctionUser("a").getFactory());
        AuctionUser user2 = authenticator.login("a", new DummyAuctionUser("a").getFactory());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        Authenticator authenticator = new Authenticator(Arrays.asList("a", "b", "c"), new DummyReadWriteLock());
        AuctionUser actualUser = authenticator.login("a", new DummyAuctionUser("a").getFactory());
        DummyAuctionUser expectedUser = new DummyAuctionUser("a");

        assertEquals(expectedUser, actualUser);
    }

    private class DummyAuctionUser extends AuctionUser {

        DummyAuctionUser(String name) {
            super(name, new HashMap<Integer, Auction>());
        }
        

        private UserFactory getFactory() {
            return new UserFactory() {
                @Override
                public AuctionUser createUser(String userName) {
                    return new DummyAuctionUser(userName);
                }
            };
        }

        @Override
        public void notifyOverbid(String description) {
        }

        @Override
        public void notifyAuctionEnd(double amount, AuctionUser winner, String description) {
        }
    }

     private class DummyReadWriteLock implements ReadWriteLock{
         @Override
         public Lock readLock() {
             return new DummyLock();
         }

         @Override
         public Lock writeLock() {
             return new DummyLock();
         }
     }

    private class DummyLock implements Lock {

        @Override
        public void lock() {
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
        }

        @Override
        public boolean tryLock() {
            return true;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return true;
        }

        @Override
        public void unlock() {
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }
}
