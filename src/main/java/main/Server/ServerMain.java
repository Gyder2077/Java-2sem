package main.Server;

import main.Model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.*;

public class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    private static final int PORT = 12346;

    private static final ForkJoinPool READING_POOL = new ForkJoinPool(4);
    private static final ExecutorService PROCESSING_POOL = Executors.newCachedThreadPool();

    public static ExecutorService getProcessingPool() { return PROCESSING_POOL; }

    public static void main(String[] args) {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            UserManager userManager = new UserManager();
            MyCollection collection = new MyCollection();

            for (Ticket t : dbManager.loadAllTickets().values()) {
                collection.addElement(t);
            }
            logger.info("Loaded {} tickets from DB", collection.getTickets().size());

            RequestHandler requestHandler = new RequestHandler(collection, dbManager, userManager);

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            ConnectionHandler connectionHandler = new ConnectionHandler();
            logger.info("Server started on port {}", PORT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) continue;

                    try {
                        if (key.isAcceptable()) {
                            SocketChannel client = connectionHandler.accept(serverChannel);
                            if (client != null) {
                                client.register(selector, SelectionKey.OP_READ);
                                requestHandler.initChannel(client);
                            }
                        } else if (key.isReadable()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            READING_POOL.submit(() -> {
                                try { requestHandler.handleRead(client); }
                                catch (IOException e) { requestHandler.closeChannel(client); }
                            });
                        }
                    } catch (IOException e) {
                        logger.error("Channel error", e);
                        key.cancel();
                        try { key.channel().close(); } catch (IOException ignored) {}
                    }
                }
            }
        } catch (IOException e) { logger.error("Critical server error", e); }
    }
}