package com.example.library.repository;

import com.example.library.domain.User;
import com.example.library.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {
    
    @Override
    public User save(User user) {
        String sql = "INSERT INTO app_user (username, password, email, role, created_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            pstmt.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DataAccessException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating user failed, no ID obtained.");
                }
            }
            
            return user;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error saving user: " + user.getUsername(), e);
        }
    }
    
    @Override
    public User update(User user) {
        String sql = "UPDATE app_user SET username = ?, password = ?, email = ?, role = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            pstmt.setInt(5, user.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DataAccessException("Updating user failed, no rows affected for userId: " + user.getUserId());
            }
            
            return user;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error updating user with id: " + user.getUserId(), e);
        }
    }
    
    @Override
    public Optional<User> findById(Integer userId) {
        String sql = "SELECT user_id, username, password, email, role, created_at FROM app_user WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by id: " + userId, e);
        }
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT user_id, username, password, email, role, created_at FROM app_user WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by username: " + username, e);
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT user_id, username, password, email, role, created_at FROM app_user WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by email: " + email, e);
        }
    }
    
    @Override
    public List<User> findAll() {
        String sql = "SELECT user_id, username, password, email, role, created_at FROM app_user ORDER BY user_id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
            return users;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all users", e);
        }
    }
    
    @Override
    public List<User> findByRole(String role) {
        String sql = "SELECT user_id, username, password, email, role, created_at FROM app_user WHERE role = ? ORDER BY user_id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            
            return users;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding users by role: " + role, e);
        }
    }
    
    @Override
    public void deleteById(Integer userId) {
        String sql = "DELETE FROM app_user WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting user with id: " + userId, e);
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM app_user WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if username exists: " + username, e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM app_user WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if email exists: " + email, e);
        }
    }
    
    /**
     * Helper method to map a ResultSet row to a User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return user;
    }
}
