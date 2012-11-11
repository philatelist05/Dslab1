package at.ac.tuwien.infosys.dslab.common.network;

import at.ac.tuwien.infosys.dslab.common.observer.Observable;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class UDPServer extends Observable<UDPNotification> implements Closeable {

    private final int bufferSize = 1400;
    private final DatagramSocket datagramSocket;
    private final ExecutorService executorService;

    public UDPServer(int port) {
        try {
            this.datagramSocket = new DatagramSocket(port);
            this.executorService = Executors.newSingleThreadExecutor();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private class MainLoop implements Runnable {
        @Override
        public void run() {
            boolean isRunning = true;

            while (isRunning) {
                try {
                    byte[] buffer = new byte[bufferSize];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(packet);
                    UDPServer.super.notifyObservers(new UDPNotification(packet, datagramSocket));
                } catch (SocketException e) {
                    /*
                       After closing the serverSocket (e.g. by another Thread),
                       serverSocket.accept() would throw a SocketException with the message below.
                       So in order to in order to escape the while loop catch the Exception
                    */
                    if (!e.getMessage().equals("Socket closed"))
                        throw new RuntimeException(e);

                    isRunning = false;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void waitForClients() {
        this.executorService.execute(new MainLoop());
    }

    @Override
    public void close() {
        this.datagramSocket.close();
        this.executorService.shutdown();
    }
}
