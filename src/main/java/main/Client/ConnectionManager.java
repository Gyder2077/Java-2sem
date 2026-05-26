package main.Client;

import main.Utils.*;

import java.io.*;
import java.net.*;

public class ConnectionManager {
    private final String host;
    private final int port;

    public ConnectionManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response sendRequest(Request request) throws IOException {
        int attempt = 0;
        while (true) {
            try {
                return trySendRequest(request);
            } catch (SocketTimeoutException e) {
                attempt++;
                if (attempt >= 5) {
                    throw new IOException("The server is unavailable after 5 attempts.", e);
                }
                System.err.println("The server is not responding, retrying " + attempt + " of 5...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted by user", ie);
                }
            }
        }
    }

    private Response trySendRequest(Request request) throws IOException {
        try (Socket socket = new Socket(host, port);
             OutputStream os = socket.getOutputStream();
             InputStream is = socket.getInputStream()) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(request);
            }
            byte[] cmdData = baos.toByteArray();

            DataOutputStream dos = new DataOutputStream(os);
            dos.writeInt(cmdData.length);
            dos.write(cmdData);
            dos.flush();

            DataInputStream dis = new DataInputStream(is);
            int respLength = dis.readInt();
            byte[] respData = new byte[respLength];
            dis.readFully(respData);

            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(respData))) {
                return (Response) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("Unknown response class", e);
            }
        }
    }
}
