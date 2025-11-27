package com.example.library.service;

import com.example.library.domain.User;
import com.example.library.repository.UserRepository;

import java.util.Optional;

/**
 * Implementation of the authentication service.
 * Provides user login and role verification functionality.
 */
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    
    /**
     * Constructs a new authentication service with the specified user repository.
     * 
     * @param userRepository the repository for accessing user data
     */
    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Authenticates a user with username and password.
     * Performs case-sensitive username lookup and exact password matching.
     * 
     * @param username the username to authenticate
     * @param password the password to verify
     * @return the authenticated User object
     * @throws AuthenticationException if user not found or password is incorrect
     */
    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be null or empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be null or empty");
        }
        
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (!userOptional.isPresent()) {
            throw new AuthenticationException("Invalid username or password");
        }
        
        User user = userOptional.get();
        
        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }
        
        return user;
    }
    
    /**
     * Checks if a user has administrator privileges.
     * Performs case-insensitive comparison with "ADMIN" role.
     * 
     * @param user the user to check
     * @return true if the user has ADMIN role, false otherwise
     * @throws IllegalArgumentException if user is null
     */
    @Override
    public boolean isAdmin(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        return user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN");
    }
}
