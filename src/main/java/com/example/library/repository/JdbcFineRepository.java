package com.example.library.repository;

import com.example.library.DatabaseConnection;
import com.example.library.domain.Fine;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcFineRepository implements FineRepository {

    @Override
    public Fine save(Fine fine) {
        String sql = "INSERT INTO fine (loan_id, amount, issued_date, status, paid_date) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, fine.getLoanId());
            pstmt.setBigDecimal(2, fine.getAmount());
            pstmt.setDate(3, Date.valueOf(fine.getIssuedDate()));
            pstmt.setString(4, fine.getStatus());
            
            if (fine.getPaidDate() != null) {
                pstmt.setDate(5, Date.valueOf(fine.getPaidDate()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DataAccessException("Creating fine failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fine.setFineId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating fine failed, no ID obtained.");
                }
            }
            
            return fine;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving fine", e);
        }
    }

    @Override
    public Fine update(Fine fine) {
        String sql = "UPDATE fine SET loan_id = ?, amount = ?, issued_date = ?, status = ?, paid_date = ? " +
                     "WHERE fine_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fine.getLoanId());
            pstmt.setBigDecimal(2, fine.getAmount());
            pstmt.setDate(3, Date.valueOf(fine.getIssuedDate()));
            pstmt.setString(4, fine.getStatus());
            
            if (fine.getPaidDate() != null) {
                pstmt.setDate(5, Date.valueOf(fine.getPaidDate()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            
            pstmt.setInt(6, fine.getFineId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DataAccessException("Updating fine failed, fine not found with id: " + fine.getFineId());
            }
            
            return fine;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating fine", e);
        }
    }

    @Override
    public Optional<Fine> findById(Integer fineId) {
        String sql = "SELECT fine_id, loan_id, amount, issued_date, status, paid_date " +
                     "FROM fine WHERE fine_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fineId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error finding fine by id: " + fineId, e);
        }
    }

    @Override
    public Optional<Fine> findByLoanId(Integer loanId) {
        String sql = "SELECT fine_id, loan_id, amount, issued_date, status, paid_date " +
                     "FROM fine WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loanId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error finding fine by loan id: " + loanId, e);
        }
    }

    @Override
    public List<Fine> findAll() {
        String sql = "SELECT fine_id, loan_id, amount, issued_date, status, paid_date " +
                     "FROM fine ORDER BY issued_date DESC";
        
        List<Fine> fines = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                fines.add(mapRow(rs));
            }
            
            return fines;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all fines", e);
        }
    }

    @Override
    public List<Fine> findByStatus(String status) {
        String sql = "SELECT fine_id, loan_id, amount, issued_date, status, paid_date " +
                     "FROM fine WHERE status = ? ORDER BY issued_date DESC";
        
        List<Fine> fines = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapRow(rs));
                }
            }
            
            return fines;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding fines by status: " + status, e);
        }
    }

    @Override
    public List<Fine> findUnpaidByUserId(Integer userId) {
        String sql = "SELECT f.fine_id, f.loan_id, f.amount, f.issued_date, f.status, f.paid_date " +
                     "FROM fine f " +
                     "JOIN loan l ON f.loan_id = l.loan_id " +
                     "WHERE l.user_id = ? AND f.status = 'UNPAID' " +
                     "ORDER BY f.issued_date DESC";
        
        List<Fine> fines = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapRow(rs));
                }
            }
            
            return fines;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding unpaid fines for user: " + userId, e);
        }
    }

    @Override
    public List<Fine> findByUserId(Integer userId) {
        String sql = "SELECT f.fine_id, f.loan_id, f.amount, f.issued_date, f.status, f.paid_date " +
                     "FROM fine f " +
                     "JOIN loan l ON f.loan_id = l.loan_id " +
                     "WHERE l.user_id = ? " +
                     "ORDER BY f.issued_date DESC";
        
        List<Fine> fines = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapRow(rs));
                }
            }
            
            return fines;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding fines for user: " + userId, e);
        }
    }

    @Override
    public BigDecimal calculateTotalUnpaidByUserId(Integer userId) {
        String sql = "SELECT COALESCE(SUM(f.amount), 0) " +
                     "FROM fine f " +
                     "JOIN loan l ON f.loan_id = l.loan_id " +
                     "WHERE l.user_id = ? AND f.status = 'UNPAID'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
            
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new DataAccessException("Error calculating total unpaid fines for user: " + userId, e);
        }
    }

    @Override
    public boolean deleteById(Integer fineId) {
        String sql = "DELETE FROM fine WHERE fine_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fineId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting fine with id: " + fineId, e);
        }
    }

    @Override
    public boolean existsByLoanId(Integer loanId) {
        String sql = "SELECT COUNT(*) FROM fine WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loanId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if fine exists for loan: " + loanId, e);
        }
    }

    @Override
    public void markAsPaid(Integer fineId, LocalDate paidDate) {
        String sql = "UPDATE fine SET status = 'PAID', paid_date = ? WHERE fine_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(paidDate));
            pstmt.setInt(2, fineId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DataAccessException("Marking fine as paid failed, fine not found with id: " + fineId);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error marking fine as paid: " + fineId, e);
        }
    }

    /**
     * Helper method to map ResultSet row to Fine object
     */
    private Fine mapRow(ResultSet rs) throws SQLException {
        Fine fine = new Fine();
        fine.setFineId(rs.getInt("fine_id"));
        fine.setLoanId(rs.getInt("loan_id"));
        fine.setAmount(rs.getBigDecimal("amount"));
        
        Date issuedDate = rs.getDate("issued_date");
        if (issuedDate != null) {
            fine.setIssuedDate(issuedDate.toLocalDate());
        }
        
        fine.setStatus(rs.getString("status"));
        
        Date paidDate = rs.getDate("paid_date");
        if (paidDate != null) {
            fine.setPaidDate(paidDate.toLocalDate());
        }
        
        return fine;
    }
}
