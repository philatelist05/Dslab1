package at.ac.tuwien.infosys.dslab.common.network;

import at.ac.tuwien.infosys.dslab.common.observer.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

public final class TCPServer extends Observable<TCPSession> implements Runnable, Closeable {

    private final ExecutorService executorService;
    private final Thread mainThread;
    private final ServerSocket serverSocket;

    public TCPServer(TCPServerFactory factory) {
        this.serverSocket = factory.createServerSocket();
        this.executorService = factory.createConnectionExecutor();
        this.mainThread = factory.createMainLoopThread(this);
    }

    @Override
    public void run() {
        try {
            boolean isRunning = true;
            while (isRunning) {
                try {
                    final Socket socket = serverSocket.accept();
                    newClientSession(socket);
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
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private void newClientSession(final Socket socket) throws IOException {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TCPServer.super.notifyObservers(new TCPSession(socket.getInputStream(), socket.getOutputStream(), socket.getInetAddress()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void waitForClients() {
        this.mainThread.start();
    }

    @Override
    public void close() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.executorService.shutdown();
        }
    }
}

