package com.hotel.management.util;

import java.sql.*;

public class DBConnectionManager {
   
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
    	if(null==connection || connection.isClosed()) {
    		connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
    	}
        return connection;
    }
}
