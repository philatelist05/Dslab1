package at.ac.tuwien.infosys.dslab.common.network;

import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class TCPSession implements Closeable {

    private final InetAddress remoteAddress;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;

    public TCPSession(InputStream inputStream, OutputStream outputStream, InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
    }

    public void writeString(String string) {
        try {
            byte[] data = string.getBytes();
            this.dataOutputStream.writeInt(data.length);
            this.dataOutputStream.write(data);
            this.dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readString() {
        try {
            int length = 0;
            byte[] data = new byte[0];
            while (length == 0) {
                length = this.dataInputStream.readInt();
                data = new byte[length];
                this.dataInputStream.read(data);
            }
            return new String(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InetAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public void close() {
        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

