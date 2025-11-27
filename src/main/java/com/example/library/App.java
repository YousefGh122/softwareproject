package com.example.library;

import java.sql.Connection;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello Library System!");
        
        // Test database connection
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Database connection test successful!");
            DatabaseConnection.closeConnection();
        } else {
            System.out.println("Failed to connect to database.");
        }
    }
}
