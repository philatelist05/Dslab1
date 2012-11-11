package at.ac.tuwien.infosys.dslab.common.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public final class UDPClient implements Closeable {

    private final DatagramSocket datagramSocket;
    private final InetAddress address;
    private final int port;

    public UDPClient(InetAddress address, int port) {
        try {
            this.datagramSocket = new DatagramSocket();
            this.address = address;
            this.port = port;
        } catch (SocketException e) {
            throw new RuntimeException("Error creating Socket", e);
        }
    }

    public void send(String message) {
        byte[] data = message.getBytes();
        try {
            this.datagramSocket.send(new DatagramPacket(data, data.length, this.address, this.port));
        } catch (IOException e) {
            throw new RuntimeException("Error sending message '" + message + "'", e);
        }
    }

    public String receive() {
        try {
            byte[] buffer = new byte[1400];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            this.datagramSocket.receive(packet);
            byte[] realData = Arrays.copyOf(packet.getData(), packet.getLength());
            return new String(realData);
        } catch (IOException e) {
            throw new RuntimeException("Error receiving Data", e);
        }
    }

    @Override
    public void close() {
        this.datagramSocket.close();
    }
}
