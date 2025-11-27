package com.example.library.service;

import com.example.library.domain.Loan;
import com.example.library.domain.MediaItem;
import com.example.library.domain.User;
import com.example.library.repository.FineRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.MediaItemRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.fine.FineCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryServiceImplTest {
    
    @Mock
    private MediaItemRepository mediaItemRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private LoanRepository loanRepository;
    
    @Mock
    private FineRepository fineRepository;
    
    @Mock
    private FineCalculator fineCalculator;
    
    private LibraryServiceImpl libraryService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        libraryService = new LibraryServiceImpl(
            userRepository,
            mediaItemRepository, 
            loanRepository, 
            fineRepository, 
            fineCalculator
        );
    }
    
    @Test
    void testAddMediaItem() {
        // Arrange
        MediaItem item = createMediaItem(null, "Test Book", "Test Author", 5, 5);
        MediaItem savedItem = createMediaItem(1, "Test Book", "Test Author", 5, 5);
        
        when(mediaItemRepository.save(item)).thenReturn(savedItem);
        
        // Act
        MediaItem result = libraryService.addMediaItem(item);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItemId());
        assertEquals("Test Book", result.getTitle());
        verify(mediaItemRepository, times(1)).save(item);
    }
    
    @Test
    void testSearchItems() {
        // Arrange
        MediaItem item1 = createMediaItem(1, "Java Programming", "Author A", 3, 2);
        MediaItem item2 = createMediaItem(2, "JavaScript Basics", "Author B", 4, 4);
        
        when(mediaItemRepository.search("Java")).thenReturn(Arrays.asList(item1, item2));
        
        // Act
        List<MediaItem> results = libraryService.searchItems("Java");
        
        // Assert
        assertEquals(2, results.size());
        assertTrue(results.get(0).getTitle().contains("Java"));
        verify(mediaItemRepository, times(1)).search("Java");
    }
    
    @Test
    void testSearchItems_NoResults() {
        // Arrange
        when(mediaItemRepository.search("nonexistent")).thenReturn(Arrays.asList());
        
        // Act
        List<MediaItem> results = libraryService.searchItems("nonexistent");
        
        // Assert
        assertTrue(results.isEmpty());
        verify(mediaItemRepository, times(1)).search("nonexistent");
    }
    
    @Test
    void testBorrowItem_Success() {
        // Arrange
        int userId = 1;
        int itemId = 10;
        LocalDate today = LocalDate.now();
        
        User user = createUser(userId, "testuser", "STUDENT");
        MediaItem item = createMediaItem(itemId, "Test Book", "Author", 5, 3);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mediaItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(loanRepository.findOverdueLoans(today)).thenReturn(Arrays.asList());
        when(fineRepository.findUnpaidByUserId(userId)).thenReturn(Arrays.asList());
        when(fineRepository.calculateTotalUnpaidByUserId(userId)).thenReturn(BigDecimal.ZERO);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setLoanId(100);
            return loan;
        });
        
        // Act
        Loan result = libraryService.borrowItem(userId, itemId, today);
        
        // Assert
        assertNotNull(result);
        assertEquals(100, result.getLoanId());
        assertEquals(userId, result.getUserId());
        assertEquals(itemId, result.getItemId());
        assertEquals("ACTIVE", result.getStatus());
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(mediaItemRepository, times(1)).updateAvailableCopies(itemId, 2);
    }
    
    @Test
    void testBorrowItem_UserNotFound() {
        // Arrange
        int userId = 999;
        int itemId = 10;
        LocalDate today = LocalDate.now();
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            libraryService.borrowItem(userId, itemId, today);
        });
        
        verify(loanRepository, never()).save(any(Loan.class));
    }
    
    @Test
    void testBorrowItem_ItemNotAvailable() {
        // Arrange
        int userId = 1;
        int itemId = 10;
        LocalDate today = LocalDate.now();
        
        User user = createUser(userId, "testuser", "STUDENT");
        MediaItem item = createMediaItem(itemId, "Test Book", "Author", 5, 0); // No copies available
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mediaItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(loanRepository.findOverdueLoans(today)).thenReturn(Arrays.asList());
        when(fineRepository.findUnpaidByUserId(userId)).thenReturn(Arrays.asList());
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            libraryService.borrowItem(userId, itemId, today);
        });
        
        verify(loanRepository, never()).save(any(Loan.class));
    }
    
    @Test
    void testGetOverdueLoans() {
        // Arrange
        LocalDate today = LocalDate.now();
        Loan overdueLoan1 = createLoan(1, 10, 100, today.minusDays(20), today.minusDays(5));
        Loan overdueLoan2 = createLoan(2, 11, 101, today.minusDays(15), today.minusDays(2));
        
        when(loanRepository.findOverdueLoans(today)).thenReturn(Arrays.asList(overdueLoan1, overdueLoan2));
        
        // Act
        List<Loan> results = libraryService.getOverdueLoans(today);
        
        // Assert
        assertEquals(2, results.size());
        assertTrue(results.get(0).getDueDate().isBefore(today));
        verify(loanRepository, times(1)).findOverdueLoans(today);
    }
    
    @Test
    void testGetUserLoans() {
        // Arrange
        int userId = 1;
        Loan loan1 = createLoan(1, userId, 100, LocalDate.now(), LocalDate.now().plusDays(14));
        Loan loan2 = createLoan(2, userId, 101, LocalDate.now(), LocalDate.now().plusDays(14));
        
        when(loanRepository.findByUserId(userId)).thenReturn(Arrays.asList(loan1, loan2));
        
        // Act
        List<Loan> results = libraryService.getUserLoans(userId);
        
        // Assert
        assertEquals(2, results.size());
        assertEquals(userId, results.get(0).getUserId());
        verify(loanRepository, times(1)).findByUserId(userId);
        verify(loanRepository, times(1)).findByUserId(userId);
    }
    
    @Test
    void testCanUserBorrow_Eligible() {
        // Arrange
        int userId = 1;
        LocalDate today = LocalDate.now();
        
        when(loanRepository.findOverdueLoans(today)).thenReturn(Arrays.asList());
        when(fineRepository.findUnpaidByUserId(userId)).thenReturn(Arrays.asList());
        when(fineRepository.calculateTotalUnpaidByUserId(userId)).thenReturn(BigDecimal.ZERO);
        
        // Act
        boolean result = libraryService.canUserBorrow(userId, today);
        
        // Assert
        assertTrue(result, "User should be eligible to borrow");
    }
    
    @Test
    void testCanUserBorrow_HasOverdueLoans() {
        // Arrange
        int userId = 1;
        LocalDate today = LocalDate.now();
        Loan overdueLoan = createLoan(1, userId, 100, today.minusDays(20), today.minusDays(5));
        
        when(loanRepository.findOverdueLoans(today)).thenReturn(Arrays.asList(overdueLoan));
        
        // Act
        boolean result = libraryService.canUserBorrow(userId, today);
        
        // Assert
        assertFalse(result, "User with overdue loans should not be eligible");
    }
    
    // Helper methods
    private MediaItem createMediaItem(Integer itemId, String title, String author, int totalCopies, int availableCopies) {
        MediaItem item = new MediaItem();
        item.setItemId(itemId);
        item.setTitle(title);
        item.setAuthor(author);
        item.setType("BOOK");
        item.setIsbn("978-1234567890");
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Test Publisher");
        item.setTotalCopies(totalCopies);
        item.setAvailableCopies(availableCopies);
        item.setLateFeesPerDay(new BigDecimal("1.00"));
        return item;
    }
    
    private User createUser(Integer userId, String username, String role) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword("password123");
        user.setEmail(username + "@example.com");
        user.setRole(role);
        return user;
    }
    
    private Loan createLoan(Integer loanId, int userId, int itemId, LocalDate loanDate, LocalDate dueDate) {
        Loan loan = new Loan();
        loan.setLoanId(loanId);
        loan.setUserId(userId);
        loan.setItemId(itemId);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setStatus("ACTIVE");
        return loan;
    }
    
    @Test
    void testSearchItems_NullKeyword_ReturnsAllItems() {
        // Arrange
        List<MediaItem> allItems = Arrays.asList(
            createMediaItem(1, "Book 1", "Author 1", 5, 3),
            createMediaItem(2, "Book 2", "Author 2", 3, 1)
        );
        when(mediaItemRepository.findAll()).thenReturn(allItems);
        
        // Act
        List<MediaItem> result = libraryService.searchItems(null);
        
        // Assert
        assertEquals(2, result.size());
        verify(mediaItemRepository).findAll();
        verify(mediaItemRepository, never()).search(anyString());
    }
    
    @Test
    void testSearchItems_EmptyKeyword_ReturnsAllItems() {
        // Arrange
        List<MediaItem> allItems = Arrays.asList(
            createMediaItem(1, "Book 1", "Author 1", 5, 3)
        );
        when(mediaItemRepository.findAll()).thenReturn(allItems);
        
        // Act
        List<MediaItem> result = libraryService.searchItems("  ");
        
        // Assert
        assertEquals(1, result.size());
        verify(mediaItemRepository).findAll();
        verify(mediaItemRepository, never()).search(anyString());
    }
}
