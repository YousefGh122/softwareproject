package com.example.library.repository;

import com.example.library.domain.User;
import com.example.library.testcontainers.TestDatabaseContainer;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JdbcUserRepository using Testcontainers.
 * Tests run against a real PostgreSQL database in Docker.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcUserRepositoryIntegrationTest {
    
    private static JdbcUserRepository userRepository;
    
    @BeforeAll
    static void setupTestContainer() {
        // Start Testcontainer and load schema
        TestDatabaseContainer.start();
        userRepository = new JdbcUserRepository();
    }
    
    @BeforeEach
    void cleanDatabase() {
        // Clean database before each test for isolation
        TestDatabaseContainer.cleanDatabase();
    }
    
    @Test
    @Order(1)
    @DisplayName("Should save a new user successfully")
    void testSave_NewUser_Success() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("hashed_password_123");
        user.setEmail("john@example.com");
        user.setRole("MEMBER");
        user.setCreatedAt(LocalDateTime.now());
        
        // Act
        userRepository.save(user);
        
        // Assert
        assertNotNull(user.getUserId(), "User ID should be generated");
        assertTrue(user.getUserId() > 0, "User ID should be positive");
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find user by ID")
    void testFindById_ExistingUser_ReturnsUser() {
        // Arrange
        User user = createAndSaveUser("jane_smith", "jane@example.com", "MEMBER");
        
        // Act
        Optional<User> found = userRepository.findById(user.getUserId());
        
        // Assert
        assertTrue(found.isPresent(), "User should be found");
        assertEquals(user.getUsername(), found.get().getUsername());
        assertEquals(user.getEmail(), found.get().getEmail());
    }
    
    @Test
    @Order(3)
    @DisplayName("Should return empty when user ID does not exist")
    void testFindById_NonExistentUser_ReturnsEmpty() {
        // Act
        Optional<User> found = userRepository.findById(9999);
        
        // Assert
        assertFalse(found.isPresent(), "Should return empty for non-existent user");
    }
    
    @Test
    @Order(4)
    @DisplayName("Should find user by username")
    void testFindByUsername_ExistingUser_ReturnsUser() {
        // Arrange
        User user = createAndSaveUser("admin_user", "admin@example.com", "ADMIN");
        
        // Act
        Optional<User> found = userRepository.findByUsername("admin_user");
        
        // Assert
        assertTrue(found.isPresent(), "User should be found by username");
        assertEquals(user.getEmail(), found.get().getEmail());
        assertEquals("ADMIN", found.get().getRole());
    }
    
    @Test
    @Order(5)
    @DisplayName("Should return empty when username does not exist")
    void testFindByUsername_NonExistentUser_ReturnsEmpty() {
        // Act
        Optional<User> found = userRepository.findByUsername("nonexistent_user");
        
        // Assert
        assertFalse(found.isPresent(), "Should return empty for non-existent username");
    }
    
    @Test
    @Order(6)
    @DisplayName("Should find user by email")
    void testFindByEmail_ExistingUser_ReturnsUser() {
        // Arrange
        User user = createAndSaveUser("librarian", "librarian@library.com", "LIBRARIAN");
        
        // Act
        Optional<User> found = userRepository.findByEmail("librarian@library.com");
        
        // Assert
        assertTrue(found.isPresent(), "User should be found by email");
        assertEquals(user.getUsername(), found.get().getUsername());
        assertEquals("LIBRARIAN", found.get().getRole());
    }
    
    @Test
    @Order(7)
    @DisplayName("Should return empty when email does not exist")
    void testFindByEmail_NonExistentEmail_ReturnsEmpty() {
        // Act
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(found.isPresent(), "Should return empty for non-existent email");
    }
    
    @Test
    @Order(8)
    @DisplayName("Should find all users")
    void testFindAll_MultipleUsers_ReturnsAllUsers() {
        // Arrange
        createAndSaveUser("user1", "user1@example.com", "MEMBER");
        createAndSaveUser("user2", "user2@example.com", "MEMBER");
        createAndSaveUser("user3", "user3@example.com", "LIBRARIAN");
        
        // Act
        List<User> users = userRepository.findAll();
        
        // Assert
        assertEquals(3, users.size(), "Should return all 3 users");
    }
    
    @Test
    @Order(9)
    @DisplayName("Should update existing user")
    void testUpdate_ExistingUser_UpdatesSuccessfully() {
        // Arrange
        User user = createAndSaveUser("old_username", "old@example.com", "MEMBER");
        Integer userId = user.getUserId();
        
        // Act
        user.setUsername("new_username");
        user.setEmail("new@example.com");
        user.setRole("LIBRARIAN");
        userRepository.update(user);
        
        // Assert
        Optional<User> updated = userRepository.findById(userId);
        assertTrue(updated.isPresent(), "Updated user should exist");
        assertEquals("new_username", updated.get().getUsername());
        assertEquals("new@example.com", updated.get().getEmail());
        assertEquals("LIBRARIAN", updated.get().getRole());
    }
    
    @Test
    @Order(10)
    @DisplayName("Should delete user by ID")
    void testDelete_ExistingUser_DeletesSuccessfully() {
        // Arrange
        User user = createAndSaveUser("to_be_deleted", "delete@example.com", "MEMBER");
        Integer userId = user.getUserId();
        
        // Act
        userRepository.deleteById(userId);
        
        // Assert
        Optional<User> deleted = userRepository.findById(userId);
        assertFalse(deleted.isPresent(), "Deleted user should not exist");
    }
    
    @Test
    @Order(11)
    @DisplayName("Should check if username exists")
    void testExistsByUsername_ExistingUser_ReturnsTrue() {
        // Arrange
        createAndSaveUser("existing_user", "existing@example.com", "MEMBER");
        
        // Act & Assert
        assertTrue(userRepository.existsByUsername("existing_user"), 
                "Should return true for existing username");
        assertFalse(userRepository.existsByUsername("nonexistent_user"), 
                "Should return false for non-existing username");
    }
    
    @Test
    @Order(12)
    @DisplayName("Should check if email exists")
    void testExistsByEmail_ExistingEmail_ReturnsTrue() {
        // Arrange
        createAndSaveUser("test_user", "test@example.com", "MEMBER");
        
        // Act & Assert
        assertTrue(userRepository.existsByEmail("test@example.com"), 
                "Should return true for existing email");
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"), 
                "Should return false for non-existing email");
    }
    
    @Test
    @Order(13)
    @DisplayName("Should enforce unique username constraint")
    void testSave_DuplicateUsername_ThrowsException() {
        // Arrange
        createAndSaveUser("duplicate_user", "user1@example.com", "MEMBER");
        
        User duplicateUser = new User();
        duplicateUser.setUsername("duplicate_user");
        duplicateUser.setPassword("password");
        duplicateUser.setEmail("user2@example.com");
        duplicateUser.setRole("MEMBER");
        duplicateUser.setCreatedAt(LocalDateTime.now());
        
        // Act & Assert
        assertThrows(DataAccessException.class, () -> userRepository.save(duplicateUser),
                "Should throw exception for duplicate username");
    }
    
    @Test
    @Order(14)
    @DisplayName("Should enforce unique email constraint")
    void testSave_DuplicateEmail_ThrowsException() {
        // Arrange
        createAndSaveUser("user1", "duplicate@example.com", "MEMBER");
        
        User duplicateUser = new User();
        duplicateUser.setUsername("user2");
        duplicateUser.setPassword("password");
        duplicateUser.setEmail("duplicate@example.com");
        duplicateUser.setRole("MEMBER");
        duplicateUser.setCreatedAt(LocalDateTime.now());
        
        // Act & Assert
        assertThrows(DataAccessException.class, () -> userRepository.save(duplicateUser),
                "Should throw exception for duplicate email");
    }
    
    // Helper method
    private User createAndSaveUser(String username, String email, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("hashed_password");
        user.setEmail(email);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }
}
