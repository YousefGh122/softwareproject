package com.example.library.repository;

import com.example.library.domain.Fine;
import com.example.library.domain.Loan;
import com.example.library.domain.MediaItem;
import com.example.library.domain.User;
import com.example.library.testcontainers.TestDatabaseContainer;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JdbcFineRepository using Testcontainers.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcFineRepositoryIntegrationTest {
    
    private static JdbcFineRepository fineRepository;
    private static JdbcLoanRepository loanRepository;
    private static JdbcUserRepository userRepository;
    private static JdbcMediaItemRepository mediaItemRepository;
    
    @BeforeAll
    static void setupTestContainer() {
        TestDatabaseContainer.start();
        fineRepository = new JdbcFineRepository();
        loanRepository = new JdbcLoanRepository();
        userRepository = new JdbcUserRepository();
        mediaItemRepository = new JdbcMediaItemRepository();
    }
    
    @BeforeEach
    void cleanDatabase() {
        TestDatabaseContainer.cleanDatabase();
    }
    
    @Test
    @Order(1)
    @DisplayName("Should save a new fine successfully")
    void testSave_NewFine_Success() {
        // Arrange
        Loan loan = createTestLoan();
        Fine fine = createFine(loan.getLoanId(), new BigDecimal("50.00"));
        
        // Act
        fineRepository.save(fine);
        
        // Assert
        assertNotNull(fine.getFineId(), "Fine ID should be generated");
        assertTrue(fine.getFineId() > 0, "Fine ID should be positive");
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find fine by ID")
    void testFindById_ExistingFine_ReturnsFine() {
        // Arrange
        Loan loan = createTestLoan();
        Fine fine = createAndSaveFine(loan.getLoanId(), new BigDecimal("30.00"));
        
        // Act
        Optional<Fine> found = fineRepository.findById(fine.getFineId());
        
        // Assert
        assertTrue(found.isPresent(), "Fine should be found");
        assertEquals(fine.getLoanId(), found.get().getLoanId());
        assertEquals(0, fine.getAmount().compareTo(found.get().getAmount()));
        assertEquals("UNPAID", found.get().getStatus());
    }
    
    @Test
    @Order(3)
    @DisplayName("Should return empty when fine ID does not exist")
    void testFindById_NonExistentFine_ReturnsEmpty() {
        // Act
        Optional<Fine> found = fineRepository.findById(9999);
        
        // Assert
        assertFalse(found.isPresent(), "Should return empty for non-existent fine");
    }
    
    @Test
    @Order(4)
    @DisplayName("Should find all fines")
    void testFindAll_MultipleFines_ReturnsAllFines() {
        // Arrange
        Loan loan1 = createTestLoan();
        Loan loan2 = createTestLoan();
        
        createAndSaveFine(loan1.getLoanId(), new BigDecimal("20.00"));
        createAndSaveFine(loan2.getLoanId(), new BigDecimal("40.00"));
        
        // Act
        List<Fine> fines = fineRepository.findAll();
        
        // Assert
        assertEquals(2, fines.size(), "Should return all 2 fines");
    }
    
    @Test
    @Order(5)
    @DisplayName("Should find fine by loan ID")
    void testFindByLoanId_ExistingLoan_ReturnsFine() {
        // Arrange
        Loan loan = createTestLoan();
        Fine fine = createAndSaveFine(loan.getLoanId(), new BigDecimal("25.00"));
        
        // Act
        Optional<Fine> found = fineRepository.findByLoanId(loan.getLoanId());
        
        // Assert
        assertTrue(found.isPresent(), "Fine should be found by loan ID");
        assertEquals(fine.getFineId(), found.get().getFineId());
        assertEquals(0, fine.getAmount().compareTo(found.get().getAmount()));
    }
    
    @Test
    @Order(6)
    @DisplayName("Should return empty when loan has no fine")
    void testFindByLoanId_LoanWithoutFine_ReturnsEmpty() {
        // Arrange
        Loan loan = createTestLoan();
        
        // Act
        Optional<Fine> found = fineRepository.findByLoanId(loan.getLoanId());
        
        // Assert
        assertFalse(found.isPresent(), "Should return empty when loan has no fine");
    }
    
    @Test
    @Order(7)
    @DisplayName("Should find fines by status")
    void testFindByStatus_ReturnsMatchingFines() {
        // Arrange
        Loan loan1 = createTestLoan();
        Loan loan2 = createTestLoan();
        Loan loan3 = createTestLoan();
        
        createAndSaveFine(loan1.getLoanId(), new BigDecimal("10.00")); // UNPAID
        createAndSaveFine(loan2.getLoanId(), new BigDecimal("20.00")); // UNPAID
        
        Fine paidFine = createAndSaveFine(loan3.getLoanId(), new BigDecimal("30.00"));
        paidFine.setStatus("PAID");
        paidFine.setPaidDate(LocalDate.now());
        fineRepository.update(paidFine);
        
        // Act
        List<Fine> unpaidFines = fineRepository.findByStatus("UNPAID");
        List<Fine> paidFines = fineRepository.findByStatus("PAID");
        
        // Assert
        assertEquals(2, unpaidFines.size(), "Should have 2 unpaid fines");
        assertEquals(1, paidFines.size(), "Should have 1 paid fine");
    }
    
    @Test
    @Order(8)
    @DisplayName("Should find unpaid fines for user")
    void testFindUnpaidByUserId_ReturnsUserUnpaidFines() {
        // Arrange
        User user1 = createAndSaveUser("user1", "user1@example.com");
        User user2 = createAndSaveUser("user2", "user2@example.com");
        MediaItem item = createAndSaveMediaItem("Book", "Author");
        
        Loan loan1 = createAndSaveLoan(user1.getUserId(), item.getItemId());
        Loan loan2 = createAndSaveLoan(user1.getUserId(), item.getItemId());
        Loan loan3 = createAndSaveLoan(user2.getUserId(), item.getItemId());
        
        createAndSaveFine(loan1.getLoanId(), new BigDecimal("15.00")); // User1 unpaid
        
        Fine user1PaidFine = createAndSaveFine(loan2.getLoanId(), new BigDecimal("25.00"));
        user1PaidFine.setStatus("PAID");
        user1PaidFine.setPaidDate(LocalDate.now());
        fineRepository.update(user1PaidFine);
        
        createAndSaveFine(loan3.getLoanId(), new BigDecimal("35.00")); // User2 unpaid
        
        // Act
        List<Fine> user1Fines = fineRepository.findUnpaidByUserId(user1.getUserId());
        List<Fine> user2Fines = fineRepository.findUnpaidByUserId(user2.getUserId());
        
        // Assert
        assertEquals(1, user1Fines.size(), "User 1 should have 1 unpaid fine");
        assertEquals(1, user2Fines.size(), "User 2 should have 1 unpaid fine");
    }
    
    @Test
    @Order(9)
    @DisplayName("Should calculate total unpaid fines for user")
    void testGetTotalUnpaidByUserId_ReturnsCorrectSum() {
        // Arrange
        User user = createAndSaveUser("user1", "user1@example.com");
        MediaItem item = createAndSaveMediaItem("Book", "Author");
        
        Loan loan1 = createAndSaveLoan(user.getUserId(), item.getItemId());
        Loan loan2 = createAndSaveLoan(user.getUserId(), item.getItemId());
        Loan loan3 = createAndSaveLoan(user.getUserId(), item.getItemId());
        
        createAndSaveFine(loan1.getLoanId(), new BigDecimal("10.50"));
        createAndSaveFine(loan2.getLoanId(), new BigDecimal("20.75"));
        
        Fine paidFine = createAndSaveFine(loan3.getLoanId(), new BigDecimal("15.00"));
        paidFine.setStatus("PAID");
        paidFine.setPaidDate(LocalDate.now());
        fineRepository.update(paidFine);
        
        // Act
        BigDecimal totalUnpaid = fineRepository.calculateTotalUnpaidByUserId(user.getUserId());
        
        // Assert
        assertEquals(0, new BigDecimal("31.25").compareTo(totalUnpaid), 
                "Total unpaid should be 31.25 (10.50 + 20.75)");
    }
    
    @Test
    @Order(10)
    @DisplayName("Should update existing fine")
    void testUpdate_ExistingFine_UpdatesSuccessfully() {
        // Arrange
        Loan loan = createTestLoan();
        Fine fine = createAndSaveFine(loan.getLoanId(), new BigDecimal("40.00"));
        Integer fineId = fine.getFineId();
        
        // Act
        fine.setAmount(new BigDecimal("50.00"));
        fine.setStatus("PAID");
        fine.setPaidDate(LocalDate.now());
        fineRepository.update(fine);
        
        // Assert
        Optional<Fine> updated = fineRepository.findById(fineId);
        assertTrue(updated.isPresent(), "Updated fine should exist");
        assertEquals("PAID", updated.get().getStatus());
        assertEquals(0, new BigDecimal("50.00").compareTo(updated.get().getAmount()));
        assertNotNull(updated.get().getPaidDate());
    }
    
    @Test
    @Order(11)
    @DisplayName("Should delete fine by ID")
    void testDelete_ExistingFine_DeletesSuccessfully() {
        // Arrange
        Loan loan = createTestLoan();
        Fine fine = createAndSaveFine(loan.getLoanId(), new BigDecimal("20.00"));
        Integer fineId = fine.getFineId();
        
        // Act
        fineRepository.deleteById(fineId);
        
        // Assert
        Optional<Fine> deleted = fineRepository.findById(fineId);
        assertFalse(deleted.isPresent(), "Deleted fine should not exist");
    }
    
    @Test
    @Order(12)
    @DisplayName("Should mark fine as paid")
    void testMarkAsPaid_UpdatesStatusAndDate() {
        // Arrange
        Loan loan = createTestLoan();
        Fine fine = createAndSaveFine(loan.getLoanId(), new BigDecimal("35.00"));
        Integer fineId = fine.getFineId();
        LocalDate paymentDate = LocalDate.now();
        
        // Act
        fineRepository.markAsPaid(fineId, paymentDate);
        
        // Assert
        Optional<Fine> updated = fineRepository.findById(fineId);
        assertTrue(updated.isPresent(), "Fine should exist");
        assertEquals("PAID", updated.get().getStatus());
        assertEquals(paymentDate, updated.get().getPaidDate());
    }
    
    @Test
    @Order(13)
    @DisplayName("Should handle cascade delete when loan is deleted")
    void testCascadeDelete_LoanDeleted_FineAlsoDeleted() {
        // Arrange
        Loan loan = createTestLoan();
        Fine fine = createAndSaveFine(loan.getLoanId(), new BigDecimal("25.00"));
        Integer fineId = fine.getFineId();
        
        // Act
        loanRepository.deleteById(loan.getLoanId());
        
        // Assert
        Optional<Fine> deletedFine = fineRepository.findById(fineId);
        assertFalse(deletedFine.isPresent(), "Fine should be deleted when loan is deleted");
    }
    
    @Test
    @Order(14)
    @DisplayName("Should handle different fine amounts with decimal precision")
    void testSave_DecimalAmounts_SavesPrecisely() {
        // Arrange
        Loan loan1 = createTestLoan();
        Loan loan2 = createTestLoan();
        Loan loan3 = createTestLoan();
        
        Fine fine1 = createAndSaveFine(loan1.getLoanId(), new BigDecimal("10.99"));
        Fine fine2 = createAndSaveFine(loan2.getLoanId(), new BigDecimal("0.50"));
        Fine fine3 = createAndSaveFine(loan3.getLoanId(), new BigDecimal("100.00"));
        
        // Act & Assert
        Optional<Fine> savedFine1 = fineRepository.findById(fine1.getFineId());
        Optional<Fine> savedFine2 = fineRepository.findById(fine2.getFineId());
        Optional<Fine> savedFine3 = fineRepository.findById(fine3.getFineId());
        
        assertTrue(savedFine1.isPresent());
        assertTrue(savedFine2.isPresent());
        assertTrue(savedFine3.isPresent());
        
        assertEquals(0, new BigDecimal("10.99").compareTo(savedFine1.get().getAmount()));
        assertEquals(0, new BigDecimal("0.50").compareTo(savedFine2.get().getAmount()));
        assertEquals(0, new BigDecimal("100.00").compareTo(savedFine3.get().getAmount()));
    }
    
    // Helper methods
    private User createAndSaveUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("hashed_password");
        user.setEmail(email);
        user.setRole("MEMBER");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }
    
    private MediaItem createAndSaveMediaItem(String title, String author) {
        MediaItem item = new MediaItem();
        item.setTitle(title);
        item.setAuthor(author);
        item.setType("BOOK");
        item.setIsbn("ISBN-" + System.nanoTime());
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Test Publisher");
        item.setTotalCopies(10);
        item.setAvailableCopies(10);
        item.setLateFeesPerDay(new BigDecimal("10.00"));
        mediaItemRepository.save(item);
        return item;
    }
    
    private Loan createAndSaveLoan(Integer userId, Integer itemId) {
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setItemId(itemId);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus("ACTIVE");
        loanRepository.save(loan);
        return loan;
    }
    
    private Loan createTestLoan() {
        User user = createAndSaveUser("user_" + System.nanoTime(), "user" + System.nanoTime() + "@example.com");
        MediaItem item = createAndSaveMediaItem("Item_" + System.nanoTime(), "Author");
        return createAndSaveLoan(user.getUserId(), item.getItemId());
    }
    
    private Fine createFine(Integer loanId, BigDecimal amount) {
        Fine fine = new Fine();
        fine.setLoanId(loanId);
        fine.setAmount(amount);
        fine.setIssuedDate(LocalDate.now());
        fine.setStatus("UNPAID");
        return fine;
    }
    
    private Fine createAndSaveFine(Integer loanId, BigDecimal amount) {
        Fine fine = createFine(loanId, amount);
        fineRepository.save(fine);
        return fine;
    }
}
