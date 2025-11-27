package com.example.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find db.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading database configuration", e);
        }
    }
    
    public static String getUrl() {
        // Allow system property override for tests (Testcontainers)
        String systemUrl = System.getProperty("db.url");
        if (systemUrl != null && !systemUrl.isEmpty()) {
            return systemUrl;
        }
        return properties.getProperty("db.url");
    }
    
    public static String getUsername() {
        // Allow system property override for tests (Testcontainers)
        String systemUsername = System.getProperty("db.username");
        if (systemUsername != null && !systemUsername.isEmpty()) {
            return systemUsername;
        }
        return properties.getProperty("db.username");
    }
    
    public static String getPassword() {
        // Allow system property override for tests (Testcontainers)
        String systemPassword = System.getProperty("db.password");
        if (systemPassword != null && !systemPassword.isEmpty()) {
            return systemPassword;
        }
        return properties.getProperty("db.password");
    }
    
    public static String getDriver() {
        return properties.getProperty("db.driver");
    }
}
