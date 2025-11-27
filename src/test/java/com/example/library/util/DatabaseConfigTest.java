package com.example.library.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    @Test
    void testGetUrl() {
        String url = DatabaseConfig.getUrl();
        assertNotNull(url, "Database URL should not be null");
        assertTrue(url.contains("jdbc"), "URL should be a JDBC URL");
    }

    @Test
    void testGetUsername() {
        String username = DatabaseConfig.getUsername();
        assertNotNull(username, "Database username should not be null");
        assertFalse(username.isEmpty(), "Database username should not be empty");
    }

    @Test
    void testGetPassword() {
        String password = DatabaseConfig.getPassword();
        assertNotNull(password, "Database password should not be null");
    }

    @Test
    void testGetDriver() {
        String driver = DatabaseConfig.getDriver();
        assertNotNull(driver, "Database driver should not be null");
        assertTrue(driver.contains("postgresql"), "Driver should be PostgreSQL driver");
    }

    @Test
    void testConfigurationIsConsistent() {
        // Verify all required properties are present
        assertNotNull(DatabaseConfig.getUrl(), "URL must be configured");
        assertNotNull(DatabaseConfig.getUsername(), "Username must be configured");
        assertNotNull(DatabaseConfig.getPassword(), "Password must be configured");
        assertNotNull(DatabaseConfig.getDriver(), "Driver must be configured");
    }

    @Test
    void testGetUrl_ContainsDatabaseName() {
        String url = DatabaseConfig.getUrl();
        assertTrue(url.contains("library_db") || url.contains("database"), 
            "URL should contain database name");
    }

    @Test
    void testGetDriver_IsPostgresDriver() {
        String driver = DatabaseConfig.getDriver();
        assertEquals("org.postgresql.Driver", driver, 
            "Driver should be PostgreSQL JDBC driver");
    }
}
