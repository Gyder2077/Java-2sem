package main.Server;

import main.Commands.*;
import main.Model.Ticket;
import main.Utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Обрабатывает входящие запросы, проверяет авторизацию и права доступа,
 * синхронизирует изменения с БД и обновляет коллекцию в памяти.
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final int MAX_MESSAGE_SIZE = 10_000_000;

    private final MyCollection collection;
    private final DatabaseManager dbManager;
    private final UserManager userManager;
    private final Map<SocketChannel, ChannelState> states = new HashMap<>();

    public RequestHandler(MyCollection collection, DatabaseManager dbManager, UserManager userManager) {
        this.collection = collection;
        this.dbManager = dbManager;
        this.userManager = userManager;
    }

    /**
     * Инициализирует состояние канала для асинхронного чтения.
     */
    public void initChannel(SocketChannel channel) {
        states.put(channel, new ChannelState(channel));
        logger.debug("Channel initialized: {}", channel.socket().getRemoteSocketAddress());
    }

    /**
     * Этап 1: Чтение данных из канала (вызывается из ForkJoinPool).
     */
    public void handleRead(SocketChannel channel) throws IOException {
        ChannelState state = states.get(channel);
        if (state == null) return;

        if (state.readingLength) {
            int bytesRead = channel.read(state.lengthBuffer);
            if (bytesRead == -1) { closeChannel(channel); return; }
            if (state.lengthBuffer.position() == ChannelState.LENGTH_SIZE) {
                state.lengthBuffer.flip();
                int msgLen = state.lengthBuffer.getInt();
                if (msgLen <= 0 || msgLen > MAX_MESSAGE_SIZE) {
                    logger.error("Invalid message size from {}", channel.getRemoteAddress());
                    closeChannel(channel);
                    return;
                }
                state.dataBuffer = ByteBuffer.allocate(msgLen);
                state.expectedLength = msgLen;
                state.readingLength = false;
            }
        } else {
            int bytesRead = channel.read(state.dataBuffer);
            if (bytesRead == -1) { closeChannel(channel); return; }
            if (state.dataBuffer.position() == state.expectedLength) {
                state.dataBuffer.flip();
                byte[] data = new byte[state.expectedLength];
                state.dataBuffer.get(data);

                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                    Request request = (Request) ois.readObject();
                    state.reset();
                    ServerMain.getProcessingPool().submit(() -> processRequest(channel, request));
                } catch (ClassNotFoundException e) {
                    logger.error("Unknown request class", e);
                    closeChannel(channel);
                }
            }
        }
    }

    /**
     * Этап 2: Выполнение в CachedThreadPool.
     * Проверяет авторизацию, делегирует выполнение команды.
     */
    private void processRequest(SocketChannel channel, Request request) {
        Response response;
        try {
            if (request.getCommand() == null) {
                response = userManager.registerOrLogin(request.getLogin(), request.getPasswordHash());
            }
            else {
                if (!userManager.isAuthenticated(request.getLogin(), request.getPasswordHash())) {
                    response = new Response("Error: authorisation needed", false);
                } else {
                    response = executeCommand(request);
                }
            }
        } catch (Exception e) {
            logger.error("Unhandled exception in request processing", e);
            response = new Response("Unexpected error", false);
        }
        Response finalResponse = response;
        new Thread(() -> sendResponse(channel, finalResponse)).start();
    }

    /**
     * Ядро обработки: проверка прав → БД → память.
     */
    private Response executeCommand(Request request) {
        Command command = request.getCommand();
        String ownerLogin = request.getLogin();

        if (isModifying(command) && !checkOwnership(command, ownerLogin)) {
            return new Response("Error: you cannot change this object", false);
        }

        if (!syncToDatabase(command, ownerLogin)) {
            return new Response("Error saving/deleting in the DB", false);
        }

        return command.execute(collection);
    }

    /**
     * Проверяет, принадлежит ли объект пользователю.
     */
    private boolean checkOwnership(Command command, String ownerLogin) {
        if (command instanceof RemoveById remove) {
            return userManager.canModify(remove.getId(), ownerLogin);
        }
        if (command instanceof Update update) {
            return userManager.canModify(update.getId(), ownerLogin);
        }
        return true;
    }

    /**
     * Синхронизация с PostgreSQL. Возвращает true, если все операции прошли успешно.
     */
    private boolean syncToDatabase(Command command, String ownerLogin) {
        try {
            if (command instanceof Add add) {
                return dbManager.insertTicket(add.getTicket(), ownerLogin);
            }
            if (command instanceof Update update) {
                return dbManager.updateTicket(update.getTicket(), ownerLogin);
            }
            if (command instanceof RemoveById remove) {
                return dbManager.deleteTicket(remove.getId(), ownerLogin);
            }
            if (command instanceof Clear || command instanceof RemoveFirst || command instanceof RemoveLower) {
                boolean allOk = true;
                List<Ticket> snapshot = new ArrayList<>(collection.getTickets());
                for (Ticket ticket : snapshot) {
                    if (userManager.canModify(ticket.getId(), ownerLogin)) {
                        allOk &= dbManager.deleteTicket(ticket.getId(), ownerLogin);
                    }
                }
                return allOk;
            }
            return true;
        } catch (Exception e) {
            logger.error("Database synchronization failed", e);
            return false;
        }
    }

    /**
     * Определяет, изменяет ли команда состояние коллекции/БД.
     */
    private boolean isModifying(Command command) {
        return command instanceof Add || command instanceof Update ||
                command instanceof RemoveById || command instanceof RemoveFirst ||
                command instanceof RemoveLower || command instanceof Clear;
    }

    /**
     * Сериализует и отправляет Response клиенту. Закрывает канал после отправки.
     */
    private void sendResponse(SocketChannel channel, Response response) {
        try {
            // Для отправки в отдельном потоке временно переводим канал в блокирующий режим
            channel.configureBlocking(true);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(response);
                byte[] data = baos.toByteArray();
                DataOutputStream dos = new DataOutputStream(channel.socket().getOutputStream());
                dos.writeInt(data.length);
                dos.write(data);
                dos.flush();
            }
            logger.debug("Response sent to {}", channel.getRemoteAddress());
        } catch (IOException e) {
            logger.error("Failed to send response", e);
        } finally {
            closeChannel(channel);
        }
    }

    /**
     * Безопасно закрывает канал и очищает состояние.
     */
    public void closeChannel(SocketChannel channel) {
        states.remove(channel);
        try {
            if (channel.isOpen()) channel.close();
        } catch (IOException e) {
            logger.warn("Error closing channel", e);
        }
    }

    /**
     * Внутренний класс для хранения состояния чтения NIO-канала.
     */
    private static class ChannelState {
        static final int LENGTH_SIZE = Integer.BYTES;
        String clientAddress;
        final ByteBuffer lengthBuffer = ByteBuffer.allocate(LENGTH_SIZE);
        ByteBuffer dataBuffer;
        int expectedLength = -1;
        boolean readingLength = true;

        ChannelState(SocketChannel ch) {
            try { clientAddress = ch.getRemoteAddress().toString(); }
            catch (IOException e) { clientAddress = "unknown"; }
        }

        void reset() {
            readingLength = true;
            lengthBuffer.clear();
            dataBuffer = null;
            expectedLength = -1;
        }
    }
}