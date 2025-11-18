package net.lodia.service.database;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.lodia.service.LodiaService;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

@Getter
@Accessors(fluent = true)
public final class DatabaseHandler {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final LodiaService service;
    private Connection connection;

    public DatabaseHandler(LodiaService service) {
        this.service = service;
        connect();
    }

    private void connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/lodia";
            String user = "user";
            String password = "password";

            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            service.getLogger().log(Level.SEVERE, "Could not connect to database!", e);
        }
    }

    public CompletableFuture<ResultSet> executeQueryAsync(String query, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement stmt = connection.prepareStatement(query);
                setParameters(stmt, params);
                return stmt.executeQuery();
            } catch (SQLException e) {
                service.getLogger().log(Level.SEVERE, "Query failed: " + e.getMessage(), e);
                return null;
            }
        }, executor);
    }

    public CompletableFuture<Integer> executeUpdateAsync(String query, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                setParameters(stmt, params);
                return stmt.executeUpdate();
            } catch (SQLException e) {
                service.getLogger().log(Level.SEVERE, "Update failed: " + e.getMessage(), e);
                return -1;
            }
        }, executor);
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            service.getLogger().log(Level.SEVERE, "Could not close database connection!", e);
        }
    }
}