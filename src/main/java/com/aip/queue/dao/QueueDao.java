package com.aip.queue.dao;

import com.aip.queue.config.Db;
import com.aip.queue.model.ServiceOption;
import com.aip.queue.model.ServiceSummary;
import com.aip.queue.model.Ticket;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Database operations for token creation, public status lookups and staff queue actions. */
public class QueueDao {

    public Ticket createTicket(String customerName, String serviceType) throws SQLException {
        if (!ServiceOption.isValid(serviceType)) {
            throw new IllegalArgumentException("Please select a valid service.");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter your name.");
        }
        String cleanName = customerName.trim();
        if (cleanName.length() > 100) {
            throw new IllegalArgumentException("Name must be 100 characters or fewer.");
        }

        try (Connection connection = Db.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Atomically increment the service's daily counter to avoid duplicate token numbers.
                String counterUpsert = "INSERT INTO token_counters (service_type, queue_date, last_number) " +
                        "VALUES (?, CURRENT_DATE(), 1) " +
                        "ON DUPLICATE KEY UPDATE last_number = last_number + 1";
                try (PreparedStatement statement = connection.prepareStatement(counterUpsert)) {
                    statement.setString(1, serviceType);
                    statement.executeUpdate();
                }

                int tokenNumber;
                String counterSelect = "SELECT last_number FROM token_counters " +
                        "WHERE service_type = ? AND queue_date = CURRENT_DATE() FOR UPDATE";
                try (PreparedStatement statement = connection.prepareStatement(counterSelect)) {
                    statement.setString(1, serviceType);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new SQLException("Could not allocate a token number.");
                        }
                        tokenNumber = result.getInt("last_number");
                    }
                }

                long ticketId;
                String insert = "INSERT INTO queue_tickets " +
                        "(service_type, token_number, customer_name, status, queue_date) " +
                        "VALUES (?, ?, ?, 'WAITING', CURRENT_DATE())";
                try (PreparedStatement statement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, serviceType);
                    statement.setInt(2, tokenNumber);
                    statement.setString(3, cleanName);
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Ticket was created but its identifier could not be read.");
                        }
                        ticketId = keys.getLong(1);
                    }
                }
                connection.commit();
                return findTicketById(ticketId);
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public List<ServiceSummary> getServiceSummaries() throws SQLException {
        List<ServiceSummary> summaries = new ArrayList<>();
        String activeSql = "SELECT token_number FROM queue_tickets " +
                "WHERE service_type = ? AND queue_date = CURRENT_DATE() AND status = 'CALLED' " +
                "ORDER BY called_at DESC LIMIT 1";
        String waitingSql = "SELECT COUNT(*) FROM queue_tickets " +
                "WHERE service_type = ? AND queue_date = CURRENT_DATE() AND status = 'WAITING'";

        try (Connection connection = Db.getConnection()) {
            for (ServiceOption service : ServiceOption.all()) {
                Integer currentToken = null;
                try (PreparedStatement statement = connection.prepareStatement(activeSql)) {
                    statement.setString(1, service.getName());
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            currentToken = result.getInt(1);
                        }
                    }
                }

                int waitingCount = 0;
                try (PreparedStatement statement = connection.prepareStatement(waitingSql)) {
                    statement.setString(1, service.getName());
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            waitingCount = result.getInt(1);
                        }
                    }
                }
                summaries.add(new ServiceSummary(service.getName(), currentToken, waitingCount));
            }
        }
        return summaries;
    }

    public Ticket findTicketForToday(String serviceType, int tokenNumber) throws SQLException {
        if (!ServiceOption.isValid(serviceType) || tokenNumber < 1) {
            return null;
        }
        String sql = "SELECT id, service_type, token_number, customer_name, status, created_at, called_at " +
                "FROM queue_tickets WHERE service_type = ? AND token_number = ? AND queue_date = CURRENT_DATE() LIMIT 1";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serviceType);
            statement.setInt(2, tokenNumber);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapTicket(result) : null;
            }
        }
    }

    public List<Ticket> getOpenQueue(String serviceType) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT id, service_type, token_number, customer_name, status, created_at, called_at " +
                "FROM queue_tickets WHERE service_type = ? AND queue_date = CURRENT_DATE() " +
                "AND status IN ('CALLED', 'WAITING') " +
                "ORDER BY CASE WHEN status = 'CALLED' THEN 0 ELSE 1 END, token_number ASC";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serviceType);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    tickets.add(mapTicket(result));
                }
            }
        }
        return tickets;
    }

    public Ticket findTicketById(long id) throws SQLException {
        String sql = "SELECT id, service_type, token_number, customer_name, status, created_at, called_at " +
                "FROM queue_tickets WHERE id = ? LIMIT 1";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapTicket(result) : null;
            }
        }
    }

    /** Returns messageCode: called, active, or empty. */
    public CallNextResult callNext(String serviceType) throws SQLException {
        if (!ServiceOption.isValid(serviceType)) {
            throw new IllegalArgumentException("Please select a valid service.");
        }
        try (Connection connection = Db.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Lock the service's daily counter row first so two staff requests cannot
                // call different tokens for the same service at the same time.
                String lockCounterSql = "SELECT last_number FROM token_counters " +
                        "WHERE service_type = ? AND queue_date = CURRENT_DATE() FOR UPDATE";
                boolean counterExists;
                try (PreparedStatement lock = connection.prepareStatement(lockCounterSql)) {
                    lock.setString(1, serviceType);
                    try (ResultSet result = lock.executeQuery()) {
                        counterExists = result.next();
                    }
                }
                if (!counterExists) {
                    connection.commit();
                    return new CallNextResult("empty", null);
                }

                String activeSql = "SELECT id, service_type, token_number, customer_name, status, created_at, called_at " +
                        "FROM queue_tickets WHERE service_type = ? AND queue_date = CURRENT_DATE() " +
                        "AND status = 'CALLED' ORDER BY called_at DESC LIMIT 1 FOR UPDATE";
                try (PreparedStatement statement = connection.prepareStatement(activeSql)) {
                    statement.setString(1, serviceType);
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            Ticket active = mapTicket(result);
                            connection.commit();
                            return new CallNextResult("active", active);
                        }
                    }
                }

                String nextSql = "SELECT id, service_type, token_number, customer_name, status, created_at, called_at " +
                        "FROM queue_tickets WHERE service_type = ? AND queue_date = CURRENT_DATE() " +
                        "AND status = 'WAITING' ORDER BY token_number ASC LIMIT 1 FOR UPDATE";
                Ticket next;
                try (PreparedStatement statement = connection.prepareStatement(nextSql)) {
                    statement.setString(1, serviceType);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            connection.commit();
                            return new CallNextResult("empty", null);
                        }
                        next = mapTicket(result);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE queue_tickets SET status = 'CALLED', called_at = CURRENT_TIMESTAMP WHERE id = ? AND status = 'WAITING'")) {
                    statement.setLong(1, next.getId());
                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("The token changed while being called. Please refresh and try again.");
                    }
                }
                connection.commit();
                next.setStatus("CALLED");
                next.setCalledAt(new Timestamp(System.currentTimeMillis()));
                return new CallNextResult("called", next);
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public boolean updateCalledTicket(long ticketId, String serviceType, String newStatus) throws SQLException {
        if (!("SERVED".equals(newStatus) || "SKIPPED".equals(newStatus))) {
            throw new IllegalArgumentException("Invalid ticket action.");
        }
        if (!ServiceOption.isValid(serviceType)) {
            throw new IllegalArgumentException("Please select a valid service.");
        }
        String sql = "UPDATE queue_tickets SET status = ?, " +
                "served_at = CASE WHEN ? = 'SERVED' THEN CURRENT_TIMESTAMP ELSE served_at END " +
                "WHERE id = ? AND service_type = ? AND queue_date = CURRENT_DATE() AND status = 'CALLED'";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus);
            statement.setString(2, newStatus);
            statement.setLong(3, ticketId);
            statement.setString(4, serviceType);
            return statement.executeUpdate() == 1;
        }
    }

    private Ticket mapTicket(ResultSet result) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(result.getLong("id"));
        ticket.setServiceType(result.getString("service_type"));
        ticket.setTokenNumber(result.getInt("token_number"));
        ticket.setCustomerName(result.getString("customer_name"));
        ticket.setStatus(result.getString("status"));
        ticket.setCreatedAt(result.getTimestamp("created_at"));
        ticket.setCalledAt(result.getTimestamp("called_at"));
        return ticket;
    }

    public static class CallNextResult {
        private final String messageCode;
        private final Ticket ticket;

        public CallNextResult(String messageCode, Ticket ticket) {
            this.messageCode = messageCode;
            this.ticket = ticket;
        }

        public String getMessageCode() { return messageCode; }
        public Ticket getTicket() { return ticket; }
    }
}
