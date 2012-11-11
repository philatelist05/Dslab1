package at.ac.tuwien.infosys.dslab.common.network;

import javax.net.SocketFactory;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class TCPClient implements Closeable {

    private final Socket socket;

    public TCPClient(SocketFactory factory) {
        try {
            this.socket = factory.createSocket();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create Socket", e);
        }
    }

    public TCPSession connect(InetAddress address, int port) {
        try {
            this.socket.connect(new InetSocketAddress(address, port));
            return new TCPSession(this.socket.getInputStream(), this.socket.getOutputStream(), this.socket.getInetAddress());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't connect to host " + address + " : " + port, e);
        }
    }

    @Override
    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
        }
    }
}
