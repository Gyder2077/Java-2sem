package main.Server;

import main.Utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

public class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try {
            FileManager fileManager = new FileManager();
            MyCollection myCollection = fileManager.parseXML();
            logger.info("XML file was read successfully");

            RequestHandler requestHandler = new RequestHandler(myCollection, fileManager);

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            ConnectionHandler connectionHandler = new ConnectionHandler();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down the server, saving the collection...");
                requestHandler.saveCollection();
            }));

            logger.info("The server is running on port {}", PORT);

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
                            Command command = requestHandler.handleRead(client);
                            if (command != null) {
                                Response response = requestHandler.processCommand(command);
                                requestHandler.prepareResponse(client, response, selector);
                            }
                        } else if (key.isWritable()) {
                            requestHandler.handleWrite(key);
                        }
                    } catch (IOException e) {
                        logger.error("ERROR processing channel", e);
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException ex) {
//                            Так надо
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Critical ERROR!", e);
        }
    }
}