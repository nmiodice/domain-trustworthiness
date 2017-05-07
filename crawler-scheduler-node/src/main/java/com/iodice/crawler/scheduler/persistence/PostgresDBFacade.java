package com.iodice.crawler.scheduler.persistence;

import com.iodice.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

class PostgresDBFacade {
    private static final Logger logger = LoggerFactory.getLogger(PostgresDBFacade.class);
    private final Connection dbConnection;
    private final HashMap<String, PreparedStatement> queryCache = new HashMap<>();

    PostgresDBFacade() {
        String host = Config.getString("postgres.host");
        String dbName = Config.getString("postgres.db_name");
        String username = Config.getString("postgres.username");
        String password = Config.getString("postgres.password");
        int port = Config.getInt("postgres.port");

        String connectionString = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", host, port, dbName,
            username, password);

        try {
            logger.info("attempting connection with: " + connectionString);
            dbConnection = DriverManager.getConnection(connectionString);
            logger.info("connected");
        } catch (Exception e) {
            throw new RuntimeException("unable to connect to postgres: " + connectionString, e);
        }
    }

    PreparedStatement preparedStatement(String sql) throws SQLException {
        if (!queryCache.containsKey(sql)) {
            logger.info(String.format("caching prepared statement for query: %n%s", sql));
            queryCache.put(sql, dbConnection.prepareStatement(sql));
        }

        return queryCache.get(sql);
    }
}
