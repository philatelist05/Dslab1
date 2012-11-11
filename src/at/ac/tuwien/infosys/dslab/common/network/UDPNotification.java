package at.ac.tuwien.infosys.dslab.common.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public final class UDPNotification {

    private final DatagramPacket packet;
    private final DatagramSocket socket;

    UDPNotification(DatagramPacket packet, DatagramSocket socket) {
        this.packet = packet;
        this.socket = socket;
    }

    public String getReceivedMessage() {
        byte[] realData = Arrays.copyOf(packet.getData(), packet.getLength());
        return new String(realData);
    }

    public void sendMessageBackToClient(String message) {
        try {
            byte[] data = message.getBytes();
            this.socket.send(new DatagramPacket(data, data.length, this.packet.getAddress(), this.packet.getPort()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
