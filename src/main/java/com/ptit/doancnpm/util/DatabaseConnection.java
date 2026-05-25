package com.ptit.doancnpm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        DatabaseConfig config = DatabaseConfig.load();
        return DriverManager.getConnection(config.url(), config.username(), config.password());
    }

    public static boolean testConnection() {
        try (Connection ignored = getConnection()) {
            return true;
        } catch (SQLException | IllegalStateException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
