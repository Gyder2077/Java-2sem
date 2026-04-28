package main.Server;

import main.Commands.*;
import main.Utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final int MAX_MESSAGE_SIZE = 10_000_000;

    private final MyCollection myCollection;
    private final FileManager fileManager;
    private final Map<SocketChannel, ChannelState> states = new HashMap<>();

    public RequestHandler(MyCollection myCollection, FileManager fileManager) {
        this.myCollection = myCollection;
        this.fileManager = fileManager;
    }

    public void initChannel(SocketChannel channel) {
        states.put(channel, new ChannelState(channel));
        logger.debug("Channel initialized {}", channel.socket().getRemoteSocketAddress());
    }

    public Command handleRead(SocketChannel channel) throws IOException {
        ChannelState state = states.get(channel);
        if (state == null) return null;

        if (state.readingLength) {
            int bytesRead = channel.read(state.lengthBuffer);
            if (bytesRead == -1) {
                closeChannel(channel);
                return null;
            }
            if (state.lengthBuffer.position() == ChannelState.LENGTH_SIZE) {
                state.lengthBuffer.flip();
                int msgLen = state.lengthBuffer.getInt();
                if (msgLen <= 0 || msgLen > MAX_MESSAGE_SIZE) {
                    logger.error("Incorrect message length {} from {}", msgLen, channel.getRemoteAddress());
                    closeChannel(channel);
                    return null;
                }
                state.dataBuffer = ByteBuffer.allocate(msgLen);
                state.expectedLength = msgLen;
                state.readingLength = false;
            }
        } else {
            int bytesRead = channel.read(state.dataBuffer);
            if (bytesRead == -1) {
                closeChannel(channel);
                return null;
            }
            if (state.dataBuffer.position() == state.expectedLength) {
                state.dataBuffer.flip();
                byte[] data = new byte[state.expectedLength];
                state.dataBuffer.get(data);

                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                    Command command = (Command) ois.readObject();
                    logger.info("Command received {} from {}", command.getClass().getSimpleName(),
                            channel.getRemoteAddress());
                    state.reset();
                    return command;
                } catch (ClassNotFoundException e) {
                    logger.error("Command class is incorrect", e);
                    closeChannel(channel);
                }
            }
        }
        return null;
    }

    public Response processCommand(Command command) {
        try {
            Response response = command.execute(myCollection);
            if (isModifyingCommand(command)) {
                saveCollection();
            }
            return response;
        } catch (Exception e) {
            logger.error("ERROR while processing command {}", command.getClass().getSimpleName(), e);
            return new Response("Server ERROR: " + e.getMessage());
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        if (buffer == null) {
            key.interestOps(SelectionKey.OP_READ);
            return;
        }

        channel.write(buffer);
        if (buffer.remaining() == 0) {
            key.attach(null);
            key.interestOps(SelectionKey.OP_READ);
            logger.debug("Response was sent successfully {}", channel.getRemoteAddress());
        }
    }

    public void prepareResponse(SocketChannel channel, Response response, Selector selector) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
        }

        byte[] data = baos.toByteArray();
        ByteBuffer writeBuffer = ByteBuffer.allocate(Integer.BYTES + data.length);
        writeBuffer.putInt(data.length);
        writeBuffer.put(data);
        writeBuffer.flip();

        channel.register(selector, SelectionKey.OP_WRITE, writeBuffer);
        logger.debug("Response was prepared for {}", channel.getRemoteAddress());
    }


    public void closeChannel(SocketChannel channel) {
        ChannelState state = states.remove(channel);
        String address = (state != null) ? state.clientAddress : "unknown";
        try {
            if (channel.isOpen()) {
                channel.close();
            }
        } catch (IOException e) {
            // подавляем
        }
        logger.info("Connection closed with {}", address);
    }

    private boolean isModifyingCommand(Command command) {
        return command instanceof Add
                || command instanceof RemoveById
                || command instanceof RemoveFirst
                || command instanceof RemoveLower
                || command instanceof Clear
                || command instanceof Update;
    }

    public void saveCollection() {
        fileManager.writeXML(myCollection);
        logger.info("Collection was saved to file");
    }

    private static class ChannelState {
        static final int LENGTH_SIZE = Integer.BYTES;
        final String clientAddress;
        final ByteBuffer lengthBuffer = ByteBuffer.allocate(LENGTH_SIZE);
        ByteBuffer dataBuffer;
        int expectedLength = -1;
        boolean readingLength = true;

        ChannelState(SocketChannel channel) {
            String addr;
            try {
                addr = channel.getRemoteAddress().toString();
            } catch (IOException e) {
                addr = "unknown";
            }
            this.clientAddress = addr;
        }

        void reset() {
            readingLength = true;
            lengthBuffer.clear();
            dataBuffer = null;
            expectedLength = -1;
        }
    }
}
