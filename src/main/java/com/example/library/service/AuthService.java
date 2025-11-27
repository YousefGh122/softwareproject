package com.example.library.service;

import com.example.library.domain.User;

/**
 * Service interface for authentication operations.
 * Handles user login and role verification.
 */
public interface AuthService {
    
    /**
     * Authenticates a user with username and password.
     * 
     * @param username the username to authenticate
     * @param password the password to verify
     * @return the authenticated User object
     * @throws AuthenticationException if authentication fails (user not found or invalid password)
     */
    User login(String username, String password);
    
    /**
     * Checks if a user has administrator privileges.
     * 
     * @param user the user to check
     * @return true if the user has ADMIN role, false otherwise
     */
    boolean isAdmin(User user);
}
