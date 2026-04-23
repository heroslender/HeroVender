package com.github.heroslender.herovender.service;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.storage.StorageType;
import com.github.heroslender.herovender.data.User;
import com.github.heroslender.herovender.storage.Database;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@RequiredArgsConstructor
public class SqlStorageService implements StorageService {
    private final HeroVender plugin;
    private Database database;

    @Override
    public void init() {
        this.database = new Database(plugin);

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS herovender_users (" +
                             "uuid VARCHAR(36) PRIMARY KEY," +
                             "shiftsell BOOLEAN NOT NULL," +
                             "autosell BOOLEAN NOT NULL" +
                             ")")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create database table", e);
        }
    }

    @Override
    public void stop() {
        if (database != null) {
            database.close();
        }
    }

    @Override
    public CompletableFuture<Void> loadUser(User user) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT shiftsell, autosell FROM herovender_users WHERE uuid = ?")) {
                stmt.setString(1, user.getPlayer().getUniqueId().toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        user.setShiftSellActive(rs.getBoolean("shiftsell"));
                        user.setAutoSellActive(rs.getBoolean("autosell"));
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load user " + user.getPlayer().getName(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveUser(User user) {
        return CompletableFuture.runAsync(() -> {
            String query;
            if (database.getStorageType() == StorageType.MYSQL) {
                query = "INSERT INTO herovender_users (uuid, shiftsell, autosell) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE shiftsell = VALUES(shiftsell), autosell = VALUES(autosell)";
            } else {
                query = "MERGE INTO herovender_users (uuid, shiftsell, autosell) KEY (uuid) VALUES (?, ?, ?)";
            }

            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user.getPlayer().getUniqueId().toString());
                stmt.setBoolean(2, user.isShiftSellActive());
                stmt.setBoolean(3, user.isAutoSellActive());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save user " + user.getPlayer().getName(), e);
            }
        });
    }
}
