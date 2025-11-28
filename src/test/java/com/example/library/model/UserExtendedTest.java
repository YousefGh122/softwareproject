package com.example.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserExtendedTest {
    
    @Test
    void testConstructorWithAllParameters() {
        User user = new User(1L, "john", "john@test.com", "pass123", "MEMBER");
        
        assertEquals(1L, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("john@test.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("MEMBER", user.getRole());
    }
    
    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
    }
    
    @Test
    void testSettersAndGetters() {
        User user = new User();
        
        user.setId(2L);
        user.setUsername("jane");
        user.setEmail("jane@test.com");
        user.setPassword("secret");
        user.setRole("ADMIN");
        
        assertEquals(2L, user.getId());
        assertEquals("jane", user.getUsername());
        assertEquals("jane@test.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertEquals("ADMIN", user.getRole());
    }
    
    @Test
    void testToString_ContainsUsername() {
        User user = new User(1L, "testuser", "test@test.com", "pass", "MEMBER");
        
        String str = user.toString();
        assertTrue(str.contains("testuser"));
    }
    
    @Test
    void testRoleValidation_Member() {
        User user = new User();
        user.setRole("MEMBER");
        
        assertEquals("MEMBER", user.getRole());
    }
    
    @Test
    void testRoleValidation_Admin() {
        User user = new User();
        user.setRole("ADMIN");
        
        assertEquals("ADMIN", user.getRole());
    }
    
    @Test
    void testRoleValidation_Librarian() {
        User user = new User();
        user.setRole("LIBRARIAN");
        
        assertEquals("LIBRARIAN", user.getRole());
    }
    
    @Test
    void testEmailFormat() {
        User user = new User();
        user.setEmail("valid@email.com");
        
        assertEquals("valid@email.com", user.getEmail());
        assertTrue(user.getEmail().contains("@"));
    }
    
    @Test
    void testPasswordHandling() {
        User user = new User();
        String password = "securePassword123";
        user.setPassword(password);
        
        assertEquals(password, user.getPassword());
        assertNotNull(user.getPassword());
    }
}
