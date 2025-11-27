package com.example.library.repository;

import com.example.library.DatabaseConnection;
import com.example.library.domain.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLoanRepository implements LoanRepository {
    
    @Override
    public Loan save(Loan loan) {
        String sql = "INSERT INTO loan (user_id, item_id, loan_date, due_date, return_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, loan.getUserId());
            pstmt.setInt(2, loan.getItemId());
            pstmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            pstmt.setDate(4, Date.valueOf(loan.getDueDate()));
            pstmt.setDate(5, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
            pstmt.setString(6, loan.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DataAccessException("Creating loan failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setLoanId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating loan failed, no ID obtained.");
                }
            }
            
            return loan;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error saving loan", e);
        }
    }
    
    @Override
    public Loan update(Loan loan) {
        String sql = "UPDATE loan SET user_id = ?, item_id = ?, loan_date = ?, due_date = ?, " +
                     "return_date = ?, status = ? WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loan.getUserId());
            pstmt.setInt(2, loan.getItemId());
            pstmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            pstmt.setDate(4, Date.valueOf(loan.getDueDate()));
            pstmt.setDate(5, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
            pstmt.setString(6, loan.getStatus());
            pstmt.setInt(7, loan.getLoanId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DataAccessException("Updating loan failed, no rows affected for loanId: " + loan.getLoanId());
            }
            
            return loan;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error updating loan with id: " + loan.getLoanId(), e);
        }
    }
    
    @Override
    public Optional<Loan> findById(Integer loanId) {
        String sql = "SELECT loan_id, user_id, item_id, loan_date, due_date, return_date, status " +
                     "FROM loan WHERE loan_id = ?";
        
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
            throw new DataAccessException("Error finding loan by id: " + loanId, e);
        }
    }
    
    @Override
    public List<Loan> findAll() {
        String sql = "SELECT loan_id, user_id, item_id, loan_date, due_date, return_date, status " +
                     "FROM loan ORDER BY loan_id";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                loans.add(mapRow(rs));
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all loans", e);
        }
    }
    
    @Override
    public List<Loan> findByUserId(Integer userId) {
        String sql = "SELECT loan_id, user_id, item_id, loan_date, due_date, return_date, status " +
                     "FROM loan WHERE user_id = ? ORDER BY loan_id";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding loans by userId: " + userId, e);
        }
    }
    
    @Override
    public List<Loan> findByItemId(Integer itemId) {
        String sql = "SELECT loan_id, user_id, item_id, loan_date, due_date, return_date, status " +
                     "FROM loan WHERE item_id = ? ORDER BY loan_id";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding loans by itemId: " + itemId, e);
        }
    }
    
    @Override
    public List<Loan> findByStatus(String status) {
        String sql = "SELECT loan_id, user_id, item_id, loan_date, due_date, return_date, status " +
                     "FROM loan WHERE status = ? ORDER BY loan_id";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding loans by status: " + status, e);
        }
    }
    
    @Override
    public List<Loan> findActiveByUserId(Integer userId) {
        String sql = "SELECT loan_id, user_id, item_id, loan_date, due_date, return_date, status " +
                     "FROM loan WHERE user_id = ? AND return_date IS NULL AND status = 'ACTIVE' ORDER BY loan_id";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active loans by userId: " + userId, e);
        }
    }
    
    @Override
    public List<Loan> findOverdueLoans(LocalDate currentDate) {
        String sql = "SELECT loan_id, user_id, item_id, loan_date, due_date, return_date, status " +
                     "FROM loan WHERE due_date < ? AND return_date IS NULL ORDER BY loan_id";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(currentDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding overdue loans", e);
        }
    }
    
    @Override
    public List<Loan> findLoansDueSoon(LocalDate currentDate, int daysAhead) {
        String sql = "SELECT loan_id, user_id, item_id, loan_date, due_date, return_date, status " +
                     "FROM loan WHERE due_date BETWEEN ? AND ? AND return_date IS NULL ORDER BY loan_id";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(currentDate));
            pstmt.setDate(2, Date.valueOf(currentDate.plusDays(daysAhead)));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error finding loans due soon", e);
        }
    }
    
    @Override
    public boolean deleteById(Integer loanId) {
        String sql = "DELETE FROM loan WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loanId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting loan with id: " + loanId, e);
        }
    }
    
    @Override
    public int countActiveByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) FROM loan WHERE user_id = ? AND return_date IS NULL AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new DataAccessException("Error counting active loans by userId: " + userId, e);
        }
    }
    
    @Override
    public void updateStatus(Integer loanId, String status, LocalDate returnDate) {
        String sql = "UPDATE loan SET status = ?, return_date = ? WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setDate(2, returnDate != null ? Date.valueOf(returnDate) : null);
            pstmt.setInt(3, loanId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DataAccessException("Updating loan status failed, no rows affected for loanId: " + loanId);
            }
            
        } catch (SQLException e) {
            throw new DataAccessException("Error updating loan status for id: " + loanId, e);
        }
    }
    
    /**
     * Helper method to map a ResultSet row to a Loan object
     */
    private Loan mapRow(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setItemId(rs.getInt("item_id"));
        
        Date loanDate = rs.getDate("loan_date");
        if (loanDate != null) {
            loan.setLoanDate(loanDate.toLocalDate());
        }
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            loan.setDueDate(dueDate.toLocalDate());
        }
        
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDate());
        }
        
        loan.setStatus(rs.getString("status"));
        
        return loan;
    }
}
