package com.example.library.repository;

import com.example.library.DatabaseConnection;
import com.example.library.domain.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcLoanRepositoryTest {
    
    private JdbcLoanRepository loanRepository;
    private Integer testUserId;
    private Integer testItemId;
    
    @BeforeEach
    void setUp() throws SQLException {
        loanRepository = new JdbcLoanRepository();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Clean in correct order: fines → loans → media_items → users
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fine")) {
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM loan")) {
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM media_item")) {
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM app_user WHERE username = 'testuser'")) {
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO app_user (username, password, email, role) VALUES (?, ?, ?, ?) RETURNING user_id")) {
                pstmt.setString(1, "testuser");
                pstmt.setString(2, "password123");
                pstmt.setString(3, "testuser@example.com");
                pstmt.setString(4, "STUDENT");
                var rs = pstmt.executeQuery();
                if (rs.next()) {
                    testUserId = rs.getInt(1);
                }
            }
            
            // Clean and insert test media item
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM media_item WHERE isbn = 'TEST-ISBN-001'")) {
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO media_item (title, author, type, isbn, publication_date, publisher, total_copies, available_copies, late_fees_per_day) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING item_id")) {
                pstmt.setString(1, "Test Book");
                pstmt.setString(2, "Test Author");
                pstmt.setString(3, "BOOK");
                pstmt.setString(4, "TEST-ISBN-001");
                pstmt.setDate(5, java.sql.Date.valueOf(LocalDate.of(2020, 1, 1)));
                pstmt.setString(6, "Test Publisher");
                pstmt.setInt(7, 5);
                pstmt.setInt(8, 5);
                pstmt.setBigDecimal(9, new BigDecimal("1.00"));
                var rs = pstmt.executeQuery();
                if (rs.next()) {
                    testItemId = rs.getInt(1);
                }
            }
        }
    }
    
    @Test
    void testSaveAndFindById() {
        // Arrange
        Loan loan = new Loan();
        loan.setUserId(testUserId);
        loan.setItemId(testItemId);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setReturnDate(null);
        loan.setStatus("ACTIVE");
        
        // Act - Save
        Loan saved = loanRepository.save(loan);
        
        // Assert - Generated ID
        assertNotNull(saved.getLoanId(), "Loan ID should be generated");
        assertTrue(saved.getLoanId() > 0, "Loan ID should be positive");
        
        // Act - Find by ID
        Optional<Loan> result = loanRepository.findById(saved.getLoanId());
        
        // Assert - Found and fields match
        assertTrue(result.isPresent(), "Loan should be found by ID");
        
        Loan found = result.get();
        assertEquals(saved.getLoanId(), found.getLoanId(), "Loan ID should match");
        assertEquals(testUserId, found.getUserId(), "User ID should match");
        assertEquals(testItemId, found.getItemId(), "Item ID should match");
        assertEquals(loan.getLoanDate(), found.getLoanDate(), "Loan date should match");
        assertEquals(loan.getDueDate(), found.getDueDate(), "Due date should match");
        assertNull(found.getReturnDate(), "Return date should be null");
        assertEquals("ACTIVE", found.getStatus(), "Status should be ACTIVE");
    }
    
    @Test
    void testFindActiveLoansByUser() {
        // Arrange - Insert two ACTIVE loans
        Loan activeLoan1 = new Loan();
        activeLoan1.setUserId(testUserId);
        activeLoan1.setItemId(testItemId);
        activeLoan1.setLoanDate(LocalDate.now().minusDays(7));
        activeLoan1.setDueDate(LocalDate.now().plusDays(7));
        activeLoan1.setReturnDate(null);
        activeLoan1.setStatus("ACTIVE");
        loanRepository.save(activeLoan1);
        
        Loan activeLoan2 = new Loan();
        activeLoan2.setUserId(testUserId);
        activeLoan2.setItemId(testItemId);
        activeLoan2.setLoanDate(LocalDate.now().minusDays(5));
        activeLoan2.setDueDate(LocalDate.now().plusDays(9));
        activeLoan2.setReturnDate(null);
        activeLoan2.setStatus("ACTIVE");
        loanRepository.save(activeLoan2);
        
        // Arrange - Insert one RETURNED loan
        Loan returnedLoan = new Loan();
        returnedLoan.setUserId(testUserId);
        returnedLoan.setItemId(testItemId);
        returnedLoan.setLoanDate(LocalDate.now().minusDays(14));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now().minusDays(1));
        returnedLoan.setStatus("RETURNED");
        loanRepository.save(returnedLoan);
        
        // Act - Find active loans
        List<Loan> activeLoans = loanRepository.findActiveByUserId(testUserId);
        int activeCount = loanRepository.countActiveByUserId(testUserId);
        
        // Assert
        assertEquals(2, activeLoans.size(), "Should find exactly 2 active loans");
        assertEquals(2, activeCount, "Active loan count should be 2");
        
        // Verify all returned loans are ACTIVE with null return_date
        for (Loan loan : activeLoans) {
            assertEquals("ACTIVE", loan.getStatus(), "All loans should have ACTIVE status");
            assertNull(loan.getReturnDate(), "All active loans should have null return date");
        }
    }
    
    @Test
    void testFindOverdueLoans() {
        // Arrange - Insert a loan with past due date and no return date
        Loan overdueLoan = new Loan();
        overdueLoan.setUserId(testUserId);
        overdueLoan.setItemId(testItemId);
        overdueLoan.setLoanDate(LocalDate.now().minusDays(20));
        overdueLoan.setDueDate(LocalDate.now().minusDays(5)); // Due date in the past
        overdueLoan.setReturnDate(null); // Not returned
        overdueLoan.setStatus("ACTIVE");
        loanRepository.save(overdueLoan);
        
        // Arrange - Insert a loan that is not overdue
        Loan currentLoan = new Loan();
        currentLoan.setUserId(testUserId);
        currentLoan.setItemId(testItemId);
        currentLoan.setLoanDate(LocalDate.now().minusDays(5));
        currentLoan.setDueDate(LocalDate.now().plusDays(9)); // Due date in the future
        currentLoan.setReturnDate(null);
        currentLoan.setStatus("ACTIVE");
        loanRepository.save(currentLoan);
        
        // Act
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());
        
        // Assert
        assertEquals(1, overdueLoans.size(), "Should find exactly 1 overdue loan");
        
        Loan found = overdueLoans.get(0);
        assertTrue(found.getDueDate().isBefore(LocalDate.now()), "Due date should be in the past");
        assertNull(found.getReturnDate(), "Return date should be null for overdue loan");
    }
    
    @Test
    void testUpdateStatus() {
        // Arrange - Insert a loan
        Loan loan = new Loan();
        loan.setUserId(testUserId);
        loan.setItemId(testItemId);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setReturnDate(null);
        loan.setStatus("ACTIVE");
        
        Loan saved = loanRepository.save(loan);
        
        // Act - Update status to RETURNED
        LocalDate returnDate = LocalDate.now();
        loanRepository.updateStatus(saved.getLoanId(), "RETURNED", returnDate);
        
        // Assert - Verify updated
        Optional<Loan> updated = loanRepository.findById(saved.getLoanId());
        assertTrue(updated.isPresent(), "Loan should still exist");
        assertEquals("RETURNED", updated.get().getStatus(), "Status should be RETURNED");
        assertEquals(returnDate, updated.get().getReturnDate(), "Return date should be set");
    }
    
    @Test
    void testFindById_NotFound() {
        // Act
        Optional<Loan> result = loanRepository.findById(99999);
        
        // Assert
        assertFalse(result.isPresent(), "Non-existent loan should not be found");
    }
    
    @Test
    void testFindByUserId() {
        // Arrange - Insert multiple loans for the same user
        Loan loan1 = new Loan();
        loan1.setUserId(testUserId);
        loan1.setItemId(testItemId);
        loan1.setLoanDate(LocalDate.now().minusDays(10));
        loan1.setDueDate(LocalDate.now().plusDays(4));
        loan1.setStatus("ACTIVE");
        loanRepository.save(loan1);
        
        Loan loan2 = new Loan();
        loan2.setUserId(testUserId);
        loan2.setItemId(testItemId);
        loan2.setLoanDate(LocalDate.now().minusDays(30));
        loan2.setDueDate(LocalDate.now().minusDays(16));
        loan2.setReturnDate(LocalDate.now().minusDays(15));
        loan2.setStatus("RETURNED");
        loanRepository.save(loan2);
        
        // Act
        List<Loan> loans = loanRepository.findByUserId(testUserId);
        
        // Assert
        assertTrue(loans.size() >= 2, "Should find at least 2 loans for user");
        assertTrue(loans.stream().anyMatch(l -> "ACTIVE".equals(l.getStatus())), "Should have active loan");
        assertTrue(loans.stream().anyMatch(l -> "RETURNED".equals(l.getStatus())), "Should have returned loan");
    }
    
    @Test
    void testCountActiveByUserId_NoActiveLoans() {
        // Arrange - Create new user with no loans
        Integer newUserId = testUserId + 1000;
        
        // Act
        int count = loanRepository.countActiveByUserId(newUserId);
        
        // Assert
        assertEquals(0, count, "New user should have 0 active loans");
    }
    
    @Test
    void testUpdateStatus_WithoutReturnDate() {
        // Arrange
        Loan loan = new Loan();
        loan.setUserId(testUserId);
        loan.setItemId(testItemId);
        loan.setLoanDate(LocalDate.now().minusDays(5));
        loan.setDueDate(LocalDate.now().plusDays(9));
        loan.setStatus("ACTIVE");
        Loan saved = loanRepository.save(loan);
        
        // Act - Update status to CANCELLED without return date
        loanRepository.updateStatus(saved.getLoanId(), "CANCELLED", null);
        
        // Assert
        Optional<Loan> updated = loanRepository.findById(saved.getLoanId());
        assertTrue(updated.isPresent(), "Loan should exist");
        assertEquals("CANCELLED", updated.get().getStatus(), "Status should be CANCELLED");
        assertNull(updated.get().getReturnDate(), "Return date should still be null");
    }
    
    @Test
    void testFindAll() {
        // Arrange - Create a loan
        Loan loan = new Loan();
        loan.setUserId(testUserId);
        loan.setItemId(testItemId);
        loan.setLoanDate(LocalDate.now().minusDays(5));
        loan.setDueDate(LocalDate.now().plusDays(9));
        loan.setStatus("ACTIVE");
        loanRepository.save(loan);
        
        // Act
        List<Loan> allLoans = loanRepository.findAll();
        
        // Assert
        assertNotNull(allLoans, "Loan list should not be null");
        assertFalse(allLoans.isEmpty(), "Should have at least one loan");
    }
    
    @Test
    void testDeleteById() {
        // Arrange
        Loan loan = new Loan();
        loan.setUserId(testUserId);
        loan.setItemId(testItemId);
        loan.setLoanDate(LocalDate.now().minusDays(3));
        loan.setDueDate(LocalDate.now().plusDays(11));
        loan.setStatus("ACTIVE");
        Loan saved = loanRepository.save(loan);
        
        // Act
        boolean deleted = loanRepository.deleteById(saved.getLoanId());
        
        // Assert
        assertTrue(deleted, "Delete operation should return true");
        Optional<Loan> found = loanRepository.findById(saved.getLoanId());
        assertFalse(found.isPresent(), "Deleted loan should not be found");
    }
    
    @Test
    void testFindByItemId() {
        // Arrange
        Loan loan1 = new Loan();
        loan1.setUserId(testUserId);
        loan1.setItemId(testItemId);
        loan1.setLoanDate(LocalDate.now().minusDays(10));
        loan1.setDueDate(LocalDate.now().plusDays(4));
        loan1.setStatus("ACTIVE");
        loanRepository.save(loan1);
        
        Loan loan2 = new Loan();
        loan2.setUserId(testUserId);
        loan2.setItemId(testItemId);
        loan2.setLoanDate(LocalDate.now().minusDays(20));
        loan2.setDueDate(LocalDate.now().minusDays(6));
        loan2.setReturnDate(LocalDate.now().minusDays(5));
        loan2.setStatus("RETURNED");
        loanRepository.save(loan2);
        
        // Act
        List<Loan> loans = loanRepository.findByItemId(testItemId);
        
        // Assert
        assertTrue(loans.size() >= 2, "Should find at least 2 loans for item");
    }
    
    @Test
    void testFindByStatus() {
        // Arrange
        Loan activeLoan = new Loan();
        activeLoan.setUserId(testUserId);
        activeLoan.setItemId(testItemId);
        activeLoan.setLoanDate(LocalDate.now().minusDays(5));
        activeLoan.setDueDate(LocalDate.now().plusDays(9));
        activeLoan.setStatus("ACTIVE");
        loanRepository.save(activeLoan);
        
        Loan returnedLoan = new Loan();
        returnedLoan.setUserId(testUserId);
        returnedLoan.setItemId(testItemId);
        returnedLoan.setLoanDate(LocalDate.now().minusDays(20));
        returnedLoan.setDueDate(LocalDate.now().minusDays(6));
        returnedLoan.setReturnDate(LocalDate.now().minusDays(5));
        returnedLoan.setStatus("RETURNED");
        loanRepository.save(returnedLoan);
        
        // Act
        List<Loan> activeLoans = loanRepository.findByStatus("ACTIVE");
        List<Loan> returnedLoans = loanRepository.findByStatus("RETURNED");
        
        // Assert
        assertFalse(activeLoans.isEmpty(), "Should have active loans");
        assertFalse(returnedLoans.isEmpty(), "Should have returned loans");
        assertTrue(activeLoans.stream().allMatch(l -> "ACTIVE".equals(l.getStatus())));
        assertTrue(returnedLoans.stream().allMatch(l -> "RETURNED".equals(l.getStatus())));
    }
}
