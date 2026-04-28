package main.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Модуль приёма подключений.
 */
public class ConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    public SocketChannel accept(ServerSocketChannel serverChannel) throws IOException {
        SocketChannel client = serverChannel.accept();
        if (client != null) {
            client.configureBlocking(false);
            logger.info("New TCP connection from {}", client.getRemoteAddress());
        }
        return client;
    }
}
