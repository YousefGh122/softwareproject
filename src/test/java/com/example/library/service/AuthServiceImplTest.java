package com.example.library.service;

import com.example.library.domain.User;
import com.example.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    private AuthServiceImpl authService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository);
    }
    
    @Test
    void testLogin_Success() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setRole("STUDENT");
        user.setCreatedAt(LocalDateTime.now());
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
        // Act
        User result = authService.login("testuser", "password123");
        
        // Assert
        assertNotNull(result, "Login should return user");
        assertEquals("testuser", result.getUsername());
        assertEquals("STUDENT", result.getRole());
        verify(userRepository, times(1)).findByUsername("testuser");
    }
    
    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.login("nonexistent", "anypassword");
        });
        
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }
    
    @Test
    void testLogin_WrongPassword() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setPassword("correctpassword");
        user.setEmail("test@example.com");
        user.setRole("STUDENT");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.login("testuser", "wrongpassword");
        });
        
        verify(userRepository, times(1)).findByUsername("testuser");
    }
    
    @Test
    void testIsAdmin_AdminUser() {
        // Arrange
        User admin = new User();
        admin.setRole("ADMIN");
        
        // Act
        boolean result = authService.isAdmin(admin);
        
        // Assert
        assertTrue(result, "User with ADMIN role should be admin");
    }
    
    @Test
    void testIsAdmin_RegularUser() {
        // Arrange
        User student = new User();
        student.setRole("STUDENT");
        
        // Act
        boolean result = authService.isAdmin(student);
        
        // Assert
        assertFalse(result, "User with STUDENT role should not be admin");
    }
    
    @Test
    void testIsAdmin_FacultyUser() {
        // Arrange
        User faculty = new User();
        faculty.setRole("FACULTY");
        
        // Act
        boolean result = authService.isAdmin(faculty);
        
        // Assert
        assertFalse(result, "User with FACULTY role should not be admin");
    }
    
    @Test
    void testLogin_EmptyUsername() {
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.login("", "password");
        });
    }
    
    @Test
    void testLogin_NullUsername() {
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.login(null, "password");
        });
    }
}
