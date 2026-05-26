package main.Server;

import main.Model.*;
import main.Model.Enums.*;
import main.Utils.PasswordHasher;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    private static final String URL = "jdbc:postgresql://localhost:5432/studs";
    private static final String USER = "s502336";
    private static final String PASSWORD = "FXdy>9719";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public long getNextTicketId() throws SQLException {
        String sql = "SELECT nextval('ticket_id_seq')";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("Cannot get ID from sequence");
        }
    }


    public Map<Long, Ticket> loadAllTickets() {
        Map<Long, Ticket> tickets = new LinkedHashMap<>();
        String sql = "SELECT * FROM tickets ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ticket ticket = mapResultSetToTicket(rs);
                tickets.put(ticket.getId(), ticket);
            }
            LOGGER.info("Loaded " + tickets.size() + " tickets from the DB");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Cannot load tickets from the DB", e);
        }
        return tickets;
    }

    public boolean insertTicket(Ticket ticket, String ownerLogin) {
        String sql = """
            INSERT INTO tickets 
            (id, name, coord_x, coord_y, creation_date, price, refundable, type,
             event_name, event_date, event_type, owner_login) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            long id = ticket.getId() > 0 ? ticket.getId() : getNextTicketId();
            ticket.idSetter(id);

            fillPreparedStatement(pstmt, ticket, id, ownerLogin);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while adding the ticket", e);
            return false;
        }
    }

    public boolean updateTicket(Ticket ticket, String ownerLogin) {
        String sql = """
            UPDATE tickets SET 
                name=?, coord_x=?, coord_y=?, creation_date=?, price=?, 
                refundable=?, type=?, event_name=?, event_date=?, event_type=?
            WHERE id=? AND owner_login=?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            fillPreparedStatement(pstmt, ticket, ticket.getId(), ownerLogin);

            pstmt.setLong(11, ticket.getId());
            pstmt.setString(12, ownerLogin);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while updating the ticket", e);
            return false;
        }
    }


    public boolean deleteTicket(long ticketId, String ownerLogin) {
        String sql = "DELETE FROM tickets WHERE id=? AND owner_login=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, ticketId);
            pstmt.setString(2, ownerLogin);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while deleting the ticket", e);
            return false;
        }
    }


    public boolean isOwner(long ticketId, String ownerLogin) {
        String sql = "SELECT 1 FROM tickets WHERE id=? AND owner_login=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, ticketId);
            pstmt.setString(2, ownerLogin);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking the owner", e);
            return false;
        }
    }


    private void fillPreparedStatement(PreparedStatement pstmt, Ticket ticket, long id, String owner)
            throws SQLException {
        pstmt.setLong(1, id);
        pstmt.setString(2, ticket.getName());
        pstmt.setDouble(3, ticket.getCoordinates().getCoordinateY());
        pstmt.setFloat(4, ticket.getCoordinates().getCoordinateX());
        pstmt.setTimestamp(5, Timestamp.from(ticket.getCreationDate().toInstant()));
        pstmt.setInt(6, ticket.getPrice());
        pstmt.setBoolean(7, ticket.getRefundable());
        pstmt.setString(8, ticket.getType() != null ? ticket.getType().name() : null);

        Event event = ticket.getEvent();
        pstmt.setString(9, event.getEventName());
        pstmt.setTimestamp(10, Timestamp.from(event.getDate().toInstant()));
        pstmt.setString(11, event.getEventType().name());
        pstmt.setString(12, owner); // owner_login
    }


    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Coordinates coords = new Coordinates((float) rs.getDouble("coord_x"),
                (double) rs.getFloat("coord_y")
        );

        ZonedDateTime creationDate = rs.getTimestamp("creation_date")
                .toInstant().atZone(java.time.ZoneId.systemDefault());

        Event event = new Event(
                rs.getLong("id"), // или отдельный id для event, если нужно
                rs.getString("event_name"),
                rs.getTimestamp("event_date").toInstant().atZone(java.time.ZoneId.systemDefault()),
                EventType.valueOf(rs.getString("event_type"))
        );

        TicketType type = null;
        String typeStr = rs.getString("type");
        if (typeStr != null) type = TicketType.valueOf(typeStr);

        return new Ticket(
                rs.getLong("id"),
                rs.getString("name"),
                coords,
                creationDate,
                rs.getInt("price"),
                rs.getBoolean("refundable"),
                type,
                event
        );
    }
}