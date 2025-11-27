package com.example.library.repository;

import com.example.library.DatabaseConnection;
import com.example.library.domain.MediaItem;
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

class JdbcMediaItemRepositoryTest {
    
    private JdbcMediaItemRepository mediaItemRepository;
    
    @BeforeEach
    void setUp() throws SQLException {
        mediaItemRepository = new JdbcMediaItemRepository();
        
        // Clean in correct order: fines → loans → media_items
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fine")) {
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM loan")) {
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM media_item")) {
                pstmt.executeUpdate();
            }
        }
    }
    
    @Test
    void testSaveAndFindById() {
        // Arrange
        MediaItem item = new MediaItem();
        item.setTitle("Effective Java");
        item.setAuthor("Joshua Bloch");
        item.setType("BOOK");
        item.setIsbn("978-0134685991");
        item.setPublicationDate(LocalDate.of(2017, 12, 27));
        item.setPublisher("Addison-Wesley");
        item.setTotalCopies(5);
        item.setAvailableCopies(5);
        item.setLateFeesPerDay(new BigDecimal("1.50"));
        
        // Act
        MediaItem saved = mediaItemRepository.save(item);
        Optional<MediaItem> result = mediaItemRepository.findById(saved.getItemId());
        
        // Assert
        assertTrue(result.isPresent(), "MediaItem should be found by ID");
        
        MediaItem found = result.get();
        assertNotNull(found.getItemId(), "Item ID should not be null");
        assertEquals("Effective Java", found.getTitle(), "Title should match");
        assertEquals("Joshua Bloch", found.getAuthor(), "Author should match");
        assertEquals("BOOK", found.getType(), "Type should match");
        assertEquals("978-0134685991", found.getIsbn(), "ISBN should match");
        assertEquals(LocalDate.of(2017, 12, 27), found.getPublicationDate(), "Publication date should match");
        assertEquals("Addison-Wesley", found.getPublisher(), "Publisher should match");
        assertEquals(5, found.getTotalCopies(), "Total copies should match");
        assertEquals(5, found.getAvailableCopies(), "Available copies should match");
        assertEquals(new BigDecimal("1.50"), found.getLateFeesPerDay(), "Late fees should match");
    }
    
    @Test
    void testSearchByKeyword() {
        // Arrange - Insert two items
        MediaItem item1 = new MediaItem();
        item1.setTitle("Java Programming");
        item1.setAuthor("John Smith");
        item1.setType("BOOK");
        item1.setIsbn("978-1234567890");
        item1.setPublicationDate(LocalDate.of(2020, 1, 1));
        item1.setPublisher("Tech Books");
        item1.setTotalCopies(3);
        item1.setAvailableCopies(3);
        item1.setLateFeesPerDay(new BigDecimal("1.00"));
        
        MediaItem item2 = new MediaItem();
        item2.setTitle("Clean Code");
        item2.setAuthor("Robert Martin");
        item2.setType("BOOK");
        item2.setIsbn("978-0132350884");
        item2.setPublicationDate(LocalDate.of(2008, 8, 1));
        item2.setPublisher("Prentice Hall");
        item2.setTotalCopies(4);
        item2.setAvailableCopies(4);
        item2.setLateFeesPerDay(new BigDecimal("1.25"));
        
        mediaItemRepository.save(item1);
        mediaItemRepository.save(item2);
        
        // Act
        List<MediaItem> results = mediaItemRepository.search("Java");
        
        // Assert
        assertEquals(1, results.size(), "Should find exactly 1 item matching 'Java'");
        assertTrue(results.get(0).getTitle().contains("Java"), "Found item title should contain 'Java'");
        assertEquals("Java Programming", results.get(0).getTitle(), "Title should be 'Java Programming'");
    }
    
    @Test
    void testUpdateAvailableCopies() {
        // Arrange - Save an item
        MediaItem item = new MediaItem();
        item.setTitle("Test Book");
        item.setAuthor("Test Author");
        item.setType("BOOK");
        item.setIsbn("978-1111111111");
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Test Publisher");
        item.setTotalCopies(10);
        item.setAvailableCopies(10);
        item.setLateFeesPerDay(new BigDecimal("1.00"));
        
        MediaItem saved = mediaItemRepository.save(item);
        
        // Act - Update available copies
        mediaItemRepository.updateAvailableCopies(saved.getItemId(), 7);
        
        // Assert - Verify updated
        Optional<MediaItem> updated = mediaItemRepository.findById(saved.getItemId());
        assertTrue(updated.isPresent(), "Item should still exist");
        assertEquals(7, updated.get().getAvailableCopies(), "Available copies should be updated to 7");
        assertEquals(10, updated.get().getTotalCopies(), "Total copies should remain 10");
    }
    
    @Test
    void testFindById_NotFound() {
        // Act
        Optional<MediaItem> result = mediaItemRepository.findById(99999);
        
        // Assert
        assertFalse(result.isPresent(), "Non-existent item should not be found");
    }
    
    @Test
    void testSearchByKeyword_NoResults() {
        // Act
        List<MediaItem> results = mediaItemRepository.search("NonExistentBook12345");
        
        // Assert
        assertTrue(results.isEmpty(), "Should return empty list for non-matching search");
    }
    
    @Test
    void testSave_UpdateExistingItem() {
        // Arrange
        MediaItem item = new MediaItem();
        item.setTitle("Original Title");
        item.setAuthor("Original Author");
        item.setType("BOOK");
        item.setIsbn("ISBN-UPDATE-001");
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Original Publisher");
        item.setTotalCopies(5);
        item.setAvailableCopies(5);
        item.setLateFeesPerDay(new BigDecimal("1.00"));
        MediaItem saved = mediaItemRepository.save(item);
        
        // Act - Update the item
        saved.setTitle("Updated Title");
        saved.setAuthor("Updated Author");
        saved.setTotalCopies(10);
        MediaItem updated = mediaItemRepository.save(saved);
        
        // Assert
        assertEquals(saved.getItemId(), updated.getItemId(), "Item ID should remain the same");
        assertEquals("Updated Title", updated.getTitle(), "Title should be updated");
        assertEquals("Updated Author", updated.getAuthor(), "Author should be updated");
        assertEquals(10, updated.getTotalCopies(), "Total copies should be updated");
    }
    
    @Test
    void testSearchByKeyword_PartialMatch() {
        // Arrange - Save multiple items
        MediaItem item1 = new MediaItem();
        item1.setTitle("Java Programming");
        item1.setAuthor("John Doe");
        item1.setType("BOOK");
        item1.setIsbn("ISBN-JAVA-001");
        item1.setPublicationDate(LocalDate.of(2020, 1, 1));
        item1.setPublisher("Tech Publisher");
        item1.setTotalCopies(5);
        item1.setAvailableCopies(5);
        item1.setLateFeesPerDay(new BigDecimal("1.00"));
        mediaItemRepository.save(item1);
        
        MediaItem item2 = new MediaItem();
        item2.setTitle("Python Programming");
        item2.setAuthor("Jane Smith");
        item2.setType("BOOK");
        item2.setIsbn("ISBN-PYTHON-001");
        item2.setPublicationDate(LocalDate.of(2021, 1, 1));
        item2.setPublisher("Tech Publisher");
        item2.setTotalCopies(3);
        item2.setAvailableCopies(3);
        item2.setLateFeesPerDay(new BigDecimal("1.50"));
        mediaItemRepository.save(item2);
        
        // Act - Search by "Programming"
        List<MediaItem> results = mediaItemRepository.search("Programming");
        
        // Assert
        assertTrue(results.size() >= 2, "Should find at least 2 items with 'Programming' in title");
        assertTrue(results.stream().anyMatch(i -> i.getTitle().contains("Java")), "Should find Java Programming");
        assertTrue(results.stream().anyMatch(i -> i.getTitle().contains("Python")), "Should find Python Programming");
    }
    
    @Test
    void testSearchByKeyword_CaseInsensitive() {
        // Arrange
        MediaItem item = new MediaItem();
        item.setTitle("Machine Learning Basics");
        item.setAuthor("AI Expert");
        item.setType("BOOK");
        item.setIsbn("ISBN-ML-001");
        item.setPublicationDate(LocalDate.of(2022, 1, 1));
        item.setPublisher("AI Publisher");
        item.setTotalCopies(4);
        item.setAvailableCopies(4);
        item.setLateFeesPerDay(new BigDecimal("2.00"));
        mediaItemRepository.save(item);
        
        // Act - Search with different cases
        List<MediaItem> results1 = mediaItemRepository.search("MACHINE");
        List<MediaItem> results2 = mediaItemRepository.search("machine");
        List<MediaItem> results3 = mediaItemRepository.search("Machine");
        
        // Assert
        assertFalse(results1.isEmpty(), "Should find item with uppercase search");
        assertFalse(results2.isEmpty(), "Should find item with lowercase search");
        assertFalse(results3.isEmpty(), "Should find item with mixed case search");
    }
    
    @Test
    void testFindAll() {
        // Act
        List<MediaItem> allItems = mediaItemRepository.findAll();
        
        // Assert
        assertNotNull(allItems, "Item list should not be null");
        // Should have at least the items created in previous tests
    }
    
    @Test
    void testDeleteById() {
        // Arrange
        MediaItem item = new MediaItem();
        item.setTitle("Delete Test Book");
        item.setAuthor("Delete Author");
        item.setType("BOOK");
        item.setIsbn("ISBN-DELETE-001");
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Delete Publisher");
        item.setTotalCopies(5);
        item.setAvailableCopies(5);
        item.setLateFeesPerDay(new BigDecimal("1.00"));
        MediaItem saved = mediaItemRepository.save(item);
        
        // Act
        boolean deleted = mediaItemRepository.deleteById(saved.getItemId());
        
        // Assert
        assertTrue(deleted, "Delete operation should return true");
        Optional<MediaItem> found = mediaItemRepository.findById(saved.getItemId());
        assertFalse(found.isPresent(), "Deleted item should not be found");
    }
    
    @Test
    void testFindByTitleContaining() {
        // Arrange
        MediaItem item = new MediaItem();
        item.setTitle("Unique Title XYZ123");
        item.setAuthor("Author");
        item.setType("BOOK");
        item.setIsbn("ISBN-TITLE-001");
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Publisher");
        item.setTotalCopies(5);
        item.setAvailableCopies(5);
        item.setLateFeesPerDay(new BigDecimal("1.00"));
        mediaItemRepository.save(item);
        
        // Act
        List<MediaItem> results = mediaItemRepository.findByTitleContaining("XYZ123");
        
        // Assert
        assertFalse(results.isEmpty(), "Should find item by title");
        assertTrue(results.stream().anyMatch(i -> i.getTitle().contains("XYZ123")));
    }
    
    @Test
    void testFindByAuthorContaining() {
        // Arrange
        MediaItem item = new MediaItem();
        item.setTitle("Book");
        item.setAuthor("Stephen King");
        item.setType("BOOK");
        item.setIsbn("ISBN-AUTHOR-001");
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Publisher");
        item.setTotalCopies(5);
        item.setAvailableCopies(5);
        item.setLateFeesPerDay(new BigDecimal("1.00"));
        mediaItemRepository.save(item);
        
        // Act
        List<MediaItem> results = mediaItemRepository.findByAuthorContaining("King");
        
        // Assert
        assertFalse(results.isEmpty(), "Should find item by author");
        assertTrue(results.stream().anyMatch(i -> i.getAuthor().contains("King")));
    }
    
    @Test
    void testFindAvailableItems() {
        // Arrange - Create available and unavailable items
        MediaItem available = new MediaItem();
        available.setTitle("Available Book");
        available.setAuthor("Author");
        available.setType("BOOK");
        available.setIsbn("ISBN-AVAIL-001");
        available.setPublicationDate(LocalDate.of(2020, 1, 1));
        available.setPublisher("Publisher");
        available.setTotalCopies(5);
        available.setAvailableCopies(3);
        available.setLateFeesPerDay(new BigDecimal("1.00"));
        mediaItemRepository.save(available);
        
        MediaItem unavailable = new MediaItem();
        unavailable.setTitle("Unavailable Book");
        unavailable.setAuthor("Author");
        unavailable.setType("BOOK");
        unavailable.setIsbn("ISBN-UNAVAIL-001");
        unavailable.setPublicationDate(LocalDate.of(2020, 1, 1));
        unavailable.setPublisher("Publisher");
        unavailable.setTotalCopies(5);
        unavailable.setAvailableCopies(0);
        unavailable.setLateFeesPerDay(new BigDecimal("1.00"));
        mediaItemRepository.save(unavailable);
        
        // Act
        List<MediaItem> results = mediaItemRepository.findAvailableItems();
        
        // Assert
        assertFalse(results.isEmpty(), "Should have available items");
        assertTrue(results.stream().allMatch(i -> i.getAvailableCopies() > 0), 
            "All items should have available copies");
    }
    
    @Test
    void testFindByIsbn() {
        // Arrange
        MediaItem item = new MediaItem();
        item.setTitle("ISBN Test Book");
        item.setAuthor("ISBN Author");
        item.setType("BOOK");
        item.setIsbn("978-ISBN-TEST-001");
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Test Publisher");
        item.setTotalCopies(3);
        item.setAvailableCopies(3);
        item.setLateFeesPerDay(new BigDecimal("1.00"));
        mediaItemRepository.save(item);
        
        // Act
        Optional<MediaItem> result = mediaItemRepository.findByIsbn("978-ISBN-TEST-001");
        
        // Assert
        assertTrue(result.isPresent(), "Item should be found by ISBN");
        assertEquals("ISBN Test Book", result.get().getTitle(), "Title should match");
        assertEquals("978-ISBN-TEST-001", result.get().getIsbn(), "ISBN should match");
    }
    
    @Test
    void testFindByIsbn_NotFound() {
        // Act
        Optional<MediaItem> result = mediaItemRepository.findByIsbn("NONEXISTENT-ISBN");
        
        // Assert
        assertFalse(result.isPresent(), "Non-existent ISBN should not be found");
    }
}
