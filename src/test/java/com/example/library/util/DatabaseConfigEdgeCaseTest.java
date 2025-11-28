package com.example.library.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigEdgeCaseTest {
    
    private String originalUrl;
    private String originalUsername;
    private String originalPassword;
    
    @BeforeEach
    void setUp() {
        // Save original values
        originalUrl = System.getProperty("db.url");
        originalUsername = System.getProperty("db.username");
        originalPassword = System.getProperty("db.password");
    }
    
    @AfterEach
    void tearDown() {
        // Restore original values
        if (originalUrl == null) {
            System.clearProperty("db.url");
        } else {
            System.setProperty("db.url", originalUrl);
        }
        
        if (originalUsername == null) {
            System.clearProperty("db.username");
        } else {
            System.setProperty("db.username", originalUsername);
        }
        
        if (originalPassword == null) {
            System.clearProperty("db.password");
        } else {
            System.setProperty("db.password", originalPassword);
        }
    }
    
    @Test
    void testGetUrl_WithEmptySystemProperty() {
        System.setProperty("db.url", "");
        String url = DatabaseConfig.getUrl();
        assertNotNull(url);
        // Should return property file value, not empty string
        assertTrue(url.contains("jdbc:postgresql"));
    }
    
    @Test
    void testGetUsername_WithEmptySystemProperty() {
        System.setProperty("db.username", "");
        String username = DatabaseConfig.getUsername();
        assertNotNull(username);
        assertFalse(username.isEmpty());
    }
    
    @Test
    void testGetPassword_WithEmptySystemProperty() {
        System.setProperty("db.password", "");
        String password = DatabaseConfig.getPassword();
        assertNotNull(password);
        assertFalse(password.isEmpty());
    }
    
    @Test
    void testGetUrl_WithValidSystemProperty() {
        System.setProperty("db.url", "jdbc:postgresql://custom:5432/customdb");
        String url = DatabaseConfig.getUrl();
        assertEquals("jdbc:postgresql://custom:5432/customdb", url);
    }
    
    @Test
    void testGetUsername_WithValidSystemProperty() {
        System.setProperty("db.username", "customuser");
        String username = DatabaseConfig.getUsername();
        assertEquals("customuser", username);
    }
    
    @Test
    void testGetPassword_WithValidSystemProperty() {
        System.setProperty("db.password", "custompass");
        String password = DatabaseConfig.getPassword();
        assertEquals("custompass", password);
    }
    
    @Test
    void testGetDriver_ReturnsPostgresDriver() {
        String driver = DatabaseConfig.getDriver();
        assertEquals("org.postgresql.Driver", driver);
    }
    
    @Test
    void testGetUrl_WithoutSystemProperty() {
        System.clearProperty("db.url");
        String url = DatabaseConfig.getUrl();
        assertNotNull(url);
        assertTrue(url.contains("jdbc:postgresql"));
    }
    
    @Test
    void testGetUsername_WithoutSystemProperty() {
        System.clearProperty("db.username");
        String username = DatabaseConfig.getUsername();
        assertNotNull(username);
        assertFalse(username.isEmpty());
    }
    
    @Test
    void testGetPassword_WithoutSystemProperty() {
        System.clearProperty("db.password");
        String password = DatabaseConfig.getPassword();
        assertNotNull(password);
        assertFalse(password.isEmpty());
    }
    
    @Test
    void testMultipleGetUrlCalls() {
        String url1 = DatabaseConfig.getUrl();
        String url2 = DatabaseConfig.getUrl();
        assertEquals(url1, url2);
    }
    
    @Test
    void testMultipleGetUsernameCalls() {
        String username1 = DatabaseConfig.getUsername();
        String username2 = DatabaseConfig.getUsername();
        assertEquals(username1, username2);
    }
    
    @Test
    void testMultipleGetPasswordCalls() {
        String password1 = DatabaseConfig.getPassword();
        String password2 = DatabaseConfig.getPassword();
        assertEquals(password1, password2);
    }
}
