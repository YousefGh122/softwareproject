package com.example.library;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionEdgeCasesTest {
    
    @Test
    void testGetConnection_ReturnsValidConnection() {
        // Act
        Connection conn = DatabaseConnection.getConnection();
        
        // Assert
        assertNotNull(conn, "Connection should not be null");
    }
    
    @Test
    void testGetConnection_ReturnsSameConnectionOnMultipleCalls() {
        // Act
        Connection conn1 = DatabaseConnection.getConnection();
        Connection conn2 = DatabaseConnection.getConnection();
        
        // Assert
        assertSame(conn1, conn2, "Should return the same connection instance");
    }
    
    @Test
    void testCloseConnection_ClosesSuccessfully() {
        // Arrange
        Connection conn = DatabaseConnection.getConnection();
        assertNotNull(conn);
        
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> DatabaseConnection.closeConnection());
    }
    
    @Test
    void testCloseConnection_WhenAlreadyClosed() {
        // Arrange - Close once
        DatabaseConnection.closeConnection();
        
        // Act & Assert - Closing again should not throw exception
        assertDoesNotThrow(() -> DatabaseConnection.closeConnection());
    }
}
