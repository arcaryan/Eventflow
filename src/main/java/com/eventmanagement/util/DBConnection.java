package com.eventmanagement.util;

import com.eventmanagement.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static final Properties PROPERTIES = loadProperties();

    private DBConnection() { }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("Missing db.properties. Copy db.properties.example to db.properties.");
            }
            properties.load(input);
            Class.forName("com.mysql.cj.jdbc.Driver");
            return properties;
        } catch (IOException | ClassNotFoundException e) {
            throw new DatabaseException("Could not load database configuration.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.username"),
                PROPERTIES.getProperty("db.password"));
    }
}
