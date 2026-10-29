package com.aip.queue.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Central JDBC connection helper. Set DB_URL, DB_USER and DB_PASSWORD as environment variables if needed. */
public final class Db {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/digital_queue?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";

    private Db() {
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("DB_URL", DEFAULT_URL);
        String user = setting("DB_USER", "root");
        String password = setting("DB_PASSWORD", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC driver was not found. Check the Maven dependencies.", ex);
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
