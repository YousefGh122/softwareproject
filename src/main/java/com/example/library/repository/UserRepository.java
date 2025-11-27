package com.example.library.repository;

import com.example.library.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    
    /**
     * Save a new user to the database
     * @param user the user to save
     * @return the saved user with generated ID
     */
    User save(User user);
    
    /**
     * Update an existing user
     * @param user the user to update
     * @return the updated user
     */
    User update(User user);
    
    /**
     * Find a user by ID
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(Integer userId);
    
    /**
     * Find a user by username
     * @param username the username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email
     * @param email the email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find all users
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * Find users by role
     * @param role the user role
     * @return list of users with the specified role
     */
    List<User> findByRole(String role);
    
    /**
     * Delete a user by ID
     * @param userId the user ID
     */
    void deleteById(Integer userId);
    
    /**
     * Check if a username exists
     * @param username the username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email exists
     * @param email the email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);
}
