package com.hospital.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to manage database connection to MySQL.
 * Maintained by: Poojitha (Database / DBMS Lead)
 */
public class DatabaseConnection {
    // Default connection configurations
    private static String url = System.getProperty("db.url", 
            System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"));
    private static String user = System.getProperty("db.user", 
            System.getenv().getOrDefault("DB_USER", "root"));
    private static String password = System.getProperty("db.password", 
            System.getenv().getOrDefault("DB_PASSWORD", "root")); // Set this to your MySQL root password

    // Static block to register MySQL driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Ensure mysql-connector-j is in the classpath.");
        }
    }

    /**
     * Set database credentials dynamically if needed (e.g. from UI settings or dialog)
     */
    public static void setCredentials(String customUrl, String customUser, String customPassword) {
        url = customUrl;
        user = customUser;
        password = customPassword;
    }

    /**
     * Establishes and returns a new active Connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Quick test to verify connectivity with MySQL server.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing MySQL Connection...");
        if (testConnection()) {
            System.out.println("✅ Successfully connected to MySQL database: hospital_db");
        } else {
            System.out.println("❌ Could not connect to MySQL. Please verify your password in DatabaseConnection.java or start MySQL service.");
        }
    }
}
