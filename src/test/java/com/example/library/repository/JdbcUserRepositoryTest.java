package com.example.library.repository;

import com.example.library.DatabaseConnection;
import com.example.library.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcUserRepositoryTest {
    
    private JdbcUserRepository userRepository;
    
    @BeforeEach
    void setUp() throws SQLException {
        userRepository = new JdbcUserRepository();
        
        // Ensure admin user exists
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if admin exists
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM app_user WHERE username = ?")) {
                checkStmt.setString(1, "admin");
                var rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    // Insert admin user
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO app_user (username, password, email, role, created_at) VALUES (?, ?, ?, ?, ?)")) {
                        insertStmt.setString(1, "admin");
                        insertStmt.setString(2, "admin123");
                        insertStmt.setString(3, "admin@library.com");
                        insertStmt.setString(4, "ADMIN");
                        insertStmt.setObject(5, LocalDateTime.now());
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
    
    @Test
    void testFindByUsername_adminExists() {
        // Act
        Optional<User> result = userRepository.findByUsername("admin");
        
        // Assert
        assertTrue(result.isPresent(), "User with username 'admin' should exist");
        
        User admin = result.get();
        assertEquals("admin", admin.getUsername(), "Username should be 'admin'");
        assertEquals("ADMIN", admin.getRole(), "Role should be 'ADMIN'");
    }
    
    @Test
    void testSaveAndFindById() throws SQLException {
        // Arrange - Clean test user
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE username = ?")) {
            pstmt.setString(1, "testuser123");
            pstmt.executeUpdate();
        }
        
        User user = new User();
        user.setUsername("testuser123");
        user.setPassword("pass123");
        user.setEmail("test@example.com");
        user.setRole("STUDENT");
        user.setCreatedAt(LocalDateTime.now());
        
        // Act
        User saved = userRepository.save(user);
        Optional<User> result = userRepository.findById(saved.getUserId());
        
        // Assert
        assertTrue(result.isPresent(), "User should be found by ID");
        User found = result.get();
        assertEquals("testuser123", found.getUsername());
        assertEquals("test@example.com", found.getEmail());
        assertEquals("STUDENT", found.getRole());
    }
    
    @Test
    void testFindByUsername_notFound() {
        // Act
        Optional<User> result = userRepository.findByUsername("nonexistentuser999");
        
        // Assert
        assertFalse(result.isPresent(), "Non-existent user should not be found");
    }
    
    @Test
    void testFindById_notFound() {
        // Act
        Optional<User> result = userRepository.findById(999999);
        
        // Assert
        assertFalse(result.isPresent(), "Non-existent user ID should not be found");
    }
    
    @Test
    void testSave_NewUser() throws SQLException {
        // Arrange - Clean test user
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE username = ?")) {
            pstmt.setString(1, "newusertest");
            pstmt.executeUpdate();
        }
        
        User user = new User();
        user.setUsername("newusertest");
        user.setPassword("pass123");
        user.setEmail("new@example.com");
        user.setRole("STUDENT");
        user.setCreatedAt(LocalDateTime.now());
        
        // Act
        User saved = userRepository.save(user);
        
        // Assert
        assertNotNull(saved.getUserId(), "User ID should be generated");
        assertEquals("newusertest", saved.getUsername(), "Username should match");
        assertEquals("new@example.com", saved.getEmail(), "Email should match");
        assertEquals("STUDENT", saved.getRole(), "Role should match");
    }
    
    @Test
    void testSave_MultipleUsers() throws SQLException {
        // Arrange - Clean test users
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE username LIKE ?")) {
            pstmt.setString(1, "multiuser%");
            pstmt.executeUpdate();
        }
        
        // Act - Save multiple users
        User user1 = new User();
        user1.setUsername("multiuser1");
        user1.setPassword("pass1");
        user1.setEmail("multi1@example.com");
        user1.setRole("STUDENT");
        user1.setCreatedAt(LocalDateTime.now());
        User saved1 = userRepository.save(user1);
        
        User user2 = new User();
        user2.setUsername("multiuser2");
        user2.setPassword("pass2");
        user2.setEmail("multi2@example.com");
        user2.setRole("FACULTY");
        user2.setCreatedAt(LocalDateTime.now());
        User saved2 = userRepository.save(user2);
        
        // Assert
        assertNotNull(saved1.getUserId(), "First user should have ID");
        assertNotNull(saved2.getUserId(), "Second user should have ID");
        assertNotEquals(saved1.getUserId(), saved2.getUserId(), "Users should have different IDs");
        
        Optional<User> found1 = userRepository.findByUsername("multiuser1");
        Optional<User> found2 = userRepository.findByUsername("multiuser2");
        
        assertTrue(found1.isPresent(), "First user should be found");
        assertTrue(found2.isPresent(), "Second user should be found");
    }
    
    @Test
    void testFindAll() {
        // Act
        List<User> allUsers = userRepository.findAll();
        
        // Assert
        assertNotNull(allUsers, "User list should not be null");
        assertFalse(allUsers.isEmpty(), "Should have at least one user (admin)");
        assertTrue(allUsers.stream().anyMatch(u -> "admin".equals(u.getUsername())), 
            "Should include admin user");
    }
    
    @Test
    void testDeleteById() throws SQLException {
        // Arrange - Create a user to delete
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE username = ?")) {
            pstmt.setString(1, "deletetest");
            pstmt.executeUpdate();
        }
        
        User user = new User();
        user.setUsername("deletetest");
        user.setPassword("pass");
        user.setEmail("delete@test.com");
        user.setRole("STUDENT");
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        
        // Act
        userRepository.deleteById(saved.getUserId());
        
        // Assert
        Optional<User> deleted = userRepository.findById(saved.getUserId());
        assertFalse(deleted.isPresent(), "Deleted user should not be found");
    }
    
    @Test
    void testUpdate() throws SQLException {
        // Arrange
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE username = ?")) {
            pstmt.setString(1, "updateuser");
            pstmt.executeUpdate();
        }
        
        User user = new User();
        user.setUsername("updateuser");
        user.setPassword("oldpass");
        user.setEmail("old@test.com");
        user.setRole("STUDENT");
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        
        // Act - Update user
        saved.setEmail("new@test.com");
        saved.setRole("FACULTY");
        saved.setPassword("newpass");
        User updated = userRepository.update(saved);
        
        // Assert
        assertEquals("new@test.com", updated.getEmail(), "Email should be updated");
        assertEquals("FACULTY", updated.getRole(), "Role should be updated");
        assertEquals("newpass", updated.getPassword(), "Password should be updated");
        
        // Verify in database
        Optional<User> found = userRepository.findById(saved.getUserId());
        assertTrue(found.isPresent());
        assertEquals("new@test.com", found.get().getEmail());
    }
    
    @Test
    void testFindByEmail() throws SQLException {
        // Arrange
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE email = ?")) {
            pstmt.setString(1, "findbyemail@test.com");
            pstmt.executeUpdate();
        }
        
        User user = new User();
        user.setUsername("emailuser");
        user.setPassword("password");
        user.setEmail("findbyemail@test.com");
        user.setRole("STUDENT");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Act
        Optional<User> result = userRepository.findByEmail("findbyemail@test.com");
        
        // Assert
        assertTrue(result.isPresent(), "User should be found by email");
        assertEquals("emailuser", result.get().getUsername(), "Username should match");
        assertEquals("findbyemail@test.com", result.get().getEmail(), "Email should match");
    }
    
    @Test
    void testFindByEmail_NotFound() {
        // Act
        Optional<User> result = userRepository.findByEmail("nonexistent@test.com");
        
        // Assert
        assertFalse(result.isPresent(), "Non-existent email should not be found");
    }
    
    @Test
    void testFindByRole() throws SQLException {
        // Arrange - Clean and create test users
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE username LIKE 'roleuser%'")) {
            pstmt.executeUpdate();
        }
        
        User student1 = new User();
        student1.setUsername("roleuser1");
        student1.setPassword("pass1");
        student1.setEmail("roleuser1@test.com");
        student1.setRole("STUDENT");
        student1.setCreatedAt(LocalDateTime.now());
        userRepository.save(student1);
        
        User student2 = new User();
        student2.setUsername("roleuser2");
        student2.setPassword("pass2");
        student2.setEmail("roleuser2@test.com");
        student2.setRole("STUDENT");
        student2.setCreatedAt(LocalDateTime.now());
        userRepository.save(student2);
        
        User faculty = new User();
        faculty.setUsername("roleuser3");
        faculty.setPassword("pass3");
        faculty.setEmail("roleuser3@test.com");
        faculty.setRole("FACULTY");
        faculty.setCreatedAt(LocalDateTime.now());
        userRepository.save(faculty);
        
        // Act
        List<User> students = userRepository.findByRole("STUDENT");
        List<User> facultyMembers = userRepository.findByRole("FACULTY");
        
        // Assert
        assertTrue(students.size() >= 2, "Should have at least 2 students");
        assertTrue(facultyMembers.size() >= 1, "Should have at least 1 faculty");
        students.forEach(s -> assertEquals("STUDENT", s.getRole(), "All should be STUDENT"));
        facultyMembers.forEach(f -> assertEquals("FACULTY", f.getRole(), "All should be FACULTY"));
    }
    
    @Test
    void testExistsByUsername() throws SQLException {
        // Arrange
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE username = ?")) {
            pstmt.setString(1, "existsuser");
            pstmt.executeUpdate();
        }
        
        User user = new User();
        user.setUsername("existsuser");
        user.setPassword("password");
        user.setEmail("exists@test.com");
        user.setRole("STUDENT");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Act & Assert
        assertTrue(userRepository.existsByUsername("existsuser"), "Username should exist");
        assertFalse(userRepository.existsByUsername("nonexistentuser999"), "Username should not exist");
    }
    
    @Test
    void testExistsByEmail() throws SQLException {
        // Arrange
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE email = ?")) {
            pstmt.setString(1, "existsemail@test.com");
            pstmt.executeUpdate();
        }
        
        User user = new User();
        user.setUsername("emailexistsuser");
        user.setPassword("password");
        user.setEmail("existsemail@test.com");
        user.setRole("STUDENT");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Act & Assert
        assertTrue(userRepository.existsByEmail("existsemail@test.com"), "Email should exist");
        assertFalse(userRepository.existsByEmail("nonexistent@test.com"), "Email should not exist");
    }
}
