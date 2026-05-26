package main.Server;

import main.Utils.Response;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Единый менеджер авторизации: регистрация, вход, проверка прав.
 * Все методы работают с хэшированными паролями.
 */
public class UserManager {
    private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());
    private static final String URL = "jdbc:postgresql://pg:5432/studs";
    private static final String DB_USER = "s123456";
    private static final String DB_PASSWORD = "your_pass";

    static {
        try { Class.forName("org.postgresql.Driver"); }
        catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL driver not found", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Регистрация или вход (логика "если нет — создай").
     * Вызывается сервером при получении Request с command == null.
     * @return Response с результатом операции
     */
    public Response registerOrLogin(String login, String passwordHash) {
        if (authenticateByHash(login, passwordHash)) {
            return new Response("Logged in successfully", true);
        }
        if (registerWithHash(login, passwordHash)) {
            return new Response("Registration is successful", true);
        }
        return new Response("Incorrect data", false);
    }

    /**
     * Проверка авторизации для обычных команд.
     */
    public boolean isAuthenticated(String login, String passwordHash) {
        return authenticateByHash(login, passwordHash);
    }

    /**
     * Проверка прав на модификацию объекта.
     */
    public boolean canModify(long ticketId, String userLogin) {
        String owner = getTicketOwner(ticketId);
        return owner != null && owner.equals(userLogin);
    }

    private boolean authenticateByHash(String login, String passwordHash) {
        String sql = "SELECT 1 FROM users WHERE login = ? AND password_hash = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, passwordHash);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Auth error", e);
            return false;
        }
    }

    private boolean registerWithHash(String login, String passwordHash) {
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, passwordHash);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                LOGGER.warning("Login already taken: " + login);
            } else {
                LOGGER.log(Level.SEVERE, "Registration error", e);
            }
            return false;
        }
    }

    private String getTicketOwner(long ticketId) {
        String sql = "SELECT owner_login FROM tickets WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, ticketId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("owner_login");
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Get owner error", e);
            return null;
        }
    }
}