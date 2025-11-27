package com.example.library.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {
    
    @Test
    void testAuthenticationException_WithMessage() {
        // Arrange
        String message = "Invalid credentials";
        
        // Act
        AuthenticationException exception = new AuthenticationException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testAuthenticationException_WithMessageAndCause() {
        // Arrange
        String message = "Authentication failed";
        Throwable cause = new RuntimeException("Database error");
        
        // Act
        AuthenticationException exception = new AuthenticationException(message, cause);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testAuthenticationException_CanBeThrown() {
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            throw new AuthenticationException("Test exception");
        });
    }
    
    @Test
    void testBusinessException_WithMessage() {
        // Arrange
        String message = "Cannot borrow more than 5 items";
        
        // Act
        BusinessException exception = new BusinessException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testBusinessException_WithMessageAndCause() {
        // Arrange
        String message = "Business rule violated";
        Throwable cause = new IllegalStateException("Invalid state");
        
        // Act
        BusinessException exception = new BusinessException(message, cause);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testBusinessException_CanBeThrown() {
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            throw new BusinessException("Test exception");
        });
    }
    
    @Test
    void testBusinessException_InheritanceChain() {
        // Arrange
        BusinessException exception = new BusinessException("Test");
        
        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testAuthenticationException_InheritanceChain() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Test");
        
        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
    }
}
