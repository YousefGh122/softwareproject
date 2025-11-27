package com.example.library.util;

import com.example.library.DatabaseConnection;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void testGetConnection_Success() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        assertNotNull(conn, "Connection should not be null");
        assertFalse(conn.isClosed(), "Connection should be open");
        conn.close();
    }

    @Test
    void testGetConnection_MultipleConnections() throws SQLException {
        Connection conn1 = DatabaseConnection.getConnection();
        Connection conn2 = DatabaseConnection.getConnection();
        
        assertNotNull(conn1, "First connection should not be null");
        assertNotNull(conn2, "Second connection should not be null");
        // Note: Connections may be the same object if connection pooling is used
        
        conn1.close();
        conn2.close();
    }

    @Test
    void testGetConnection_ValidDatabaseUrl() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String url = conn.getMetaData().getURL();
        
        assertTrue(url.contains("postgresql"), "URL should contain postgresql");
        assertTrue(url.contains("library_db"), "URL should contain library_db database name");
        
        conn.close();
    }

    @Test
    void testGetConnection_CanExecuteQuery() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        
        // Test that we can execute a simple query
        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT 1");
        assertTrue(rs.next(), "Query should return at least one row");
        assertEquals(1, rs.getInt(1), "Query should return 1");
        
        rs.close();
        stmt.close();
        conn.close();
    }
}
