package com.example.library.util;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigSystemPropertiesTest {
    
    private String originalUrl;
    private String originalUsername;
    private String originalPassword;
    
    @BeforeEach
    void setUp() {
        // Save original system properties
        originalUrl = System.getProperty("db.url");
        originalUsername = System.getProperty("db.username");
        originalPassword = System.getProperty("db.password");
    }
    
    @AfterEach
    void tearDown() {
        // Restore original system properties
        if (originalUrl != null) {
            System.setProperty("db.url", originalUrl);
        } else {
            System.clearProperty("db.url");
        }
        
        if (originalUsername != null) {
            System.setProperty("db.username", originalUsername);
        } else {
            System.clearProperty("db.username");
        }
        
        if (originalPassword != null) {
            System.setProperty("db.password", originalPassword);
        } else {
            System.clearProperty("db.password");
        }
    }
    
    @Test
    void testSystemPropertyOverride_Url() {
        String customUrl = "jdbc:postgresql://custom:5432/testdb";
        System.setProperty("db.url", customUrl);
        
        assertEquals(customUrl, DatabaseConfig.getUrl());
    }
    
    @Test
    void testSystemPropertyOverride_Username() {
        String customUsername = "custom_user";
        System.setProperty("db.username", customUsername);
        
        assertEquals(customUsername, DatabaseConfig.getUsername());
    }
    
    @Test
    void testSystemPropertyOverride_Password() {
        String customPassword = "custom_pass";
        System.setProperty("db.password", customPassword);
        
        assertEquals(customPassword, DatabaseConfig.getPassword());
    }
    
    @Test
    void testSystemPropertyOverride_EmptyString() {
        System.setProperty("db.url", "");
        
        // Should fall back to properties file
        assertNotNull(DatabaseConfig.getUrl());
        assertFalse(DatabaseConfig.getUrl().isEmpty());
    }
    
    @Test
    void testNoSystemPropertyOverride() {
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
        
        // Should use properties from file
        assertNotNull(DatabaseConfig.getUrl());
        assertNotNull(DatabaseConfig.getUsername());
        assertNotNull(DatabaseConfig.getPassword());
    }
    
    @Test
    void testDriver_NoSystemPropertyOverride() {
        // Driver should always come from properties file
        String driver = DatabaseConfig.getDriver();
        assertNotNull(driver);
        assertEquals("org.postgresql.Driver", driver);
    }
    
    @Test
    void testAllSystemPropertiesOverride() {
        System.setProperty("db.url", "jdbc:postgresql://test:5432/db");
        System.setProperty("db.username", "testuser");
        System.setProperty("db.password", "testpass");
        
        assertEquals("jdbc:postgresql://test:5432/db", DatabaseConfig.getUrl());
        assertEquals("testuser", DatabaseConfig.getUsername());
        assertEquals("testpass", DatabaseConfig.getPassword());
    }
}
