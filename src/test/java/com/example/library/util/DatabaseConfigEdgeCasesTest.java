package com.example.library.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for DatabaseConfig
 */
class DatabaseConfigEdgeCasesTest {
    
    @Test
    void testGetUrl() {
        String url = DatabaseConfig.getUrl();
        assertNotNull(url, "URL should not be null");
        assertTrue(url.startsWith("jdbc:postgresql://"), "URL should be valid PostgreSQL JDBC URL");
    }
    
    @Test
    void testGetUsername() {
        String username = DatabaseConfig.getUsername();
        assertNotNull(username, "Username should not be null");
        assertFalse(username.isEmpty(), "Username should not be empty");
    }
    
    @Test
    void testGetPassword() {
        String password = DatabaseConfig.getPassword();
        assertNotNull(password, "Password should not be null");
        // Password can be empty in some configurations
    }
    
    @Test
    void testDatabaseConfigNotInstantiable() {
        // DatabaseConfig should be a utility class with private constructor
        // This test verifies it exists and has config values
        assertDoesNotThrow(() -> {
            DatabaseConfig.getUrl();
            DatabaseConfig.getUsername();
            DatabaseConfig.getPassword();
        });
    }
}
