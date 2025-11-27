package com.example.library.repository;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class DataAccessExceptionTest {
    
    @Test
    void testDataAccessException_WithMessage() {
        // Arrange
        String message = "Database query failed";
        
        // Act
        DataAccessException exception = new DataAccessException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testDataAccessException_WithMessageAndCause() {
        // Arrange
        String message = "Failed to save entity";
        SQLException cause = new SQLException("Connection timeout");
        
        // Act
        DataAccessException exception = new DataAccessException(message, cause);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testDataAccessException_WithCauseOnly() {
        // Arrange
        SQLException cause = new SQLException("Unique constraint violation");
        
        // Act
        DataAccessException exception = new DataAccessException(cause);
        
        // Assert
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("SQLException"));
    }
    
    @Test
    void testDataAccessException_CanBeThrown() {
        // Act & Assert
        assertThrows(DataAccessException.class, () -> {
            throw new DataAccessException("Test exception");
        });
    }
    
    @Test
    void testDataAccessException_InheritanceChain() {
        // Arrange
        DataAccessException exception = new DataAccessException("Test");
        
        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
    }
}
