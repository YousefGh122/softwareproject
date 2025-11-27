package com.example.library.notification;

import com.example.library.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotifierTest {
    
    private EmailNotifier emailNotifier;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        emailNotifier = new EmailNotifier();
        
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setRole("STUDENT");
        testUser.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void testNotify_Success() {
        // Arrange
        String message = "Your book is overdue!";
        
        // Act
        emailNotifier.notify(testUser, message);
        
        // Assert
        assertEquals(1, emailNotifier.getSentMessages().size());
        assertTrue(emailNotifier.getSentMessages().get(0).contains("testuser@example.com"));
        assertTrue(emailNotifier.getSentMessages().get(0).contains(message));
    }
    
    @Test
    void testNotify_MultipleMessages() {
        // Arrange
        String message1 = "Your book is due tomorrow";
        String message2 = "Your book is overdue";
        
        // Act
        emailNotifier.notify(testUser, message1);
        emailNotifier.notify(testUser, message2);
        
        // Assert
        assertEquals(2, emailNotifier.getSentMessages().size());
        assertTrue(emailNotifier.getSentMessages().get(0).contains(message1));
        assertTrue(emailNotifier.getSentMessages().get(1).contains(message2));
    }
    
    @Test
    void testNotify_NullUser() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotifier.notify(null, "Test message");
        });
    }
    
    @Test
    void testNotify_NullEmail() {
        // Arrange
        testUser.setEmail(null);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotifier.notify(testUser, "Test message");
        });
    }
    
    @Test
    void testNotify_EmptyEmail() {
        // Arrange
        testUser.setEmail("   ");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotifier.notify(testUser, "Test message");
        });
    }
    
    @Test
    void testNotify_NullMessage() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotifier.notify(testUser, null);
        });
    }
    
    @Test
    void testNotify_EmptyMessage() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotifier.notify(testUser, "   ");
        });
    }
    
    @Test
    void testGetSentMessages_ReturnsUnmodifiableList() {
        // Arrange
        emailNotifier.notify(testUser, "Test message");
        
        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            emailNotifier.getSentMessages().add("Should not work");
        });
    }
    
    @Test
    void testClearMessages() {
        // Arrange
        emailNotifier.notify(testUser, "Message 1");
        emailNotifier.notify(testUser, "Message 2");
        assertEquals(2, emailNotifier.getSentMessages().size());
        
        // Act
        emailNotifier.clearMessages();
        
        // Assert
        assertEquals(0, emailNotifier.getSentMessages().size());
    }
    
    @Test
    void testNotify_DifferentUsers() {
        // Arrange
        User user2 = new User();
        user2.setUserId(2);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRole("FACULTY");
        
        // Act
        emailNotifier.notify(testUser, "Message for user 1");
        emailNotifier.notify(user2, "Message for user 2");
        
        // Assert
        assertEquals(2, emailNotifier.getSentMessages().size());
        assertTrue(emailNotifier.getSentMessages().get(0).contains("testuser@example.com"));
        assertTrue(emailNotifier.getSentMessages().get(1).contains("user2@example.com"));
    }
}
