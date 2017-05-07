package com.iodice.crawler.scheduler.persistence;

import com.iodice.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class PostgresDBFacade {
    private static final Connection dbConnection;

    static {
        String host = Config.getString("postgres.host");
        String dbName = Config.getString("postgres.db_name");
        String username = Config.getString("postgres.username");
        String password = Config.getString("postgres.password");
        int port = Config.getInt("postgres.port");

        String connectionString = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", host, port, dbName,
            username, password);

        try {
            dbConnection = DriverManager.getConnection(connectionString);
        } catch (Exception e) {
            throw new RuntimeException("unable to connect to postgres: " + connectionString, e);
        }
    }

    static PreparedStatement preparedStatement(String sql) throws SQLException {
        return dbConnection.prepareStatement(sql);
    }

}
