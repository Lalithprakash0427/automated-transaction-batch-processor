package com.banking.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {

    // rewriteBatchedStatements=true lets the MySQL driver collapse a batch into
    // multi-row INSERTs, which is what makes batching fast on MySQL.
    private static final String URL = getEnv("DB_URL",
            "jdbc:mysql://localhost:3306/banking_db"
                    + "?rewriteBatchedStatements=true&useSSL=false&serverTimezone=UTC");
    private static final String USER = getEnv("DB_USER", "root");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "your_password");

    private DatabaseConfig() {
        // utility class
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}