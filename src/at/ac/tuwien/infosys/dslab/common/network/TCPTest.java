package at.ac.tuwien.infosys.dslab.common.network;

import at.ac.tuwien.infosys.dslab.common.observer.*;
import org.junit.After;
import org.junit.Test;

import javax.net.SocketFactory;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class TCPTest {

    protected final TCPServer server;
    private final TCPClient client;

    public TCPTest() throws Exception {
        this.server = new TCPServer(new TCPServerFactory(4500));
        client = new TCPClient(SocketFactory.getDefault());
    }

    @After
    public void tearDown() throws Exception {
        server.close();
        client.close();
    }

    @Test
    public void testShouldWriteData() throws Exception {
        final String expected = "Test";
        server.addObserver(new Observer<TCPSession>() {
            @Override
            public void update(Observable observable, TCPSession session) {

                session.writeString(expected);
                synchronized (expected) {
                    expected.notifyAll();
                }

            }
        });

        server.waitForClients();
        TCPSession session = client.connect(InetAddress.getLocalHost(), 4500);

        String actual = session.readString();
        synchronized (expected) {
            expected.wait(2000);
        }

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldReadData() throws Exception {
        final String[] actual = {""};
        String expected = "Test";

        server.addObserver(new Observer<TCPSession>() {
            @Override
            public void update(Observable observable, TCPSession session) {
                actual[0] = session.readString();
                synchronized (actual) {
                    actual.notifyAll();
                }

            }
        });

        server.waitForClients();
        TCPSession session = client.connect(InetAddress.getLocalHost(), 4500);

        session.writeString(expected);
        synchronized (actual) {
            actual.wait(2000);
        }
        assertEquals(expected, actual[0]);
    }

}

