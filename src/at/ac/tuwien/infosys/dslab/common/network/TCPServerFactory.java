package at.ac.tuwien.infosys.dslab.common.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TCPServerFactory {

    private final int port;

    public TCPServerFactory(int port) {
        this.port = port;
    }

    public ServerSocket createServerSocket() {
        try {
            return new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ExecutorService createConnectionExecutor() {
        return Executors.newCachedThreadPool();
    }

    public Thread createMainLoopThread(Runnable runnable) {
        return new Thread(runnable);
    }
}

