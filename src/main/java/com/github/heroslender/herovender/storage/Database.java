package com.github.heroslender.herovender.storage;

import com.github.heroslender.herovender.Config;
import com.github.heroslender.herovender.HeroVender;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.val;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private final HeroVender plugin;
    private HikariDataSource dataSource;
    @Getter
    private final StorageType storageType;

    public Database(HeroVender plugin) {
        this.plugin = plugin;
        val config = plugin.getConfig();
        this.storageType = StorageType.fromName(config.getString("storage.type", "H2"));
        
        initDataSource();
    }

    private void initDataSource() {
        val config = plugin.getConfig();
        HikariConfig hikariConfig = new HikariConfig();

        if (storageType == StorageType.MYSQL) {
            val host = config.getString("storage.mysql.host");
            val port = config.getInt("storage.mysql.port");
            val database = config.getString("storage.mysql.database");
            val username = config.getString("storage.mysql.username");
            val password = config.getString("storage.mysql.password");

            hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        } else {
            val file = plugin.getDataFolder().toPath().resolve("data");
            hikariConfig.setJdbcUrl("jdbc:h2:" + file.toAbsolutePath());
            hikariConfig.setDriverClassName(storageType.getDriverClass());
        }

        hikariConfig.setPoolName("HeroVenderPool");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTimeout(5000);

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
