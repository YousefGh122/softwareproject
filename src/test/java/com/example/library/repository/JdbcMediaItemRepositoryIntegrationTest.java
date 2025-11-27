package com.example.library.repository;

import com.example.library.domain.MediaItem;
import com.example.library.testcontainers.TestDatabaseContainer;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JdbcMediaItemRepository using Testcontainers.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcMediaItemRepositoryIntegrationTest {
    
    private static JdbcMediaItemRepository mediaItemRepository;
    
    @BeforeAll
    static void setupTestContainer() {
        TestDatabaseContainer.start();
        mediaItemRepository = new JdbcMediaItemRepository();
    }
    
    @BeforeEach
    void cleanDatabase() {
        TestDatabaseContainer.cleanDatabase();
    }
    
    @Test
    @Order(1)
    @DisplayName("Should save a new media item successfully")
    void testSave_NewMediaItem_Success() {
        // Arrange
        MediaItem item = createMediaItem("Clean Code", "Robert Martin", "BOOK", "978-0132350884");
        
        // Act
        mediaItemRepository.save(item);
        
        // Assert
        assertNotNull(item.getItemId(), "Item ID should be generated");
        assertTrue(item.getItemId() > 0, "Item ID should be positive");
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find media item by ID")
    void testFindById_ExistingItem_ReturnsItem() {
        // Arrange
        MediaItem item = createAndSaveMediaItem("Effective Java", "Joshua Bloch", "BOOK", "978-0134685991");
        
        // Act
        Optional<MediaItem> found = mediaItemRepository.findById(item.getItemId());
        
        // Assert
        assertTrue(found.isPresent(), "Media item should be found");
        assertEquals(item.getTitle(), found.get().getTitle());
        assertEquals(item.getAuthor(), found.get().getAuthor());
    }
    
    @Test
    @Order(3)
    @DisplayName("Should return empty when item ID does not exist")
    void testFindById_NonExistentItem_ReturnsEmpty() {
        // Act
        Optional<MediaItem> found = mediaItemRepository.findById(9999);
        
        // Assert
        assertFalse(found.isPresent(), "Should return empty for non-existent item");
    }
    
    @Test
    @Order(4)
    @DisplayName("Should find all media items")
    void testFindAll_MultipleItems_ReturnsAllItems() {
        // Arrange
        createAndSaveMediaItem("Book 1", "Author 1", "BOOK", "ISBN-001");
        createAndSaveMediaItem("Book 2", "Author 2", "BOOK", "ISBN-002");
        createAndSaveMediaItem("CD 1", "Artist 1", "CD", "CD-001");
        
        // Act
        List<MediaItem> items = mediaItemRepository.findAll();
        
        // Assert
        assertEquals(3, items.size(), "Should return all 3 items");
    }
    
    @Test
    @Order(5)
    @DisplayName("Should search items by title")
    void testSearch_ByTitle_ReturnsMatchingItems() {
        // Arrange
        createAndSaveMediaItem("Java Programming", "Author A", "BOOK", "ISBN-A");
        createAndSaveMediaItem("Python Programming", "Author B", "BOOK", "ISBN-B");
        createAndSaveMediaItem("JavaScript Guide", "Author C", "BOOK", "ISBN-C");
        
        // Act
        List<MediaItem> results = mediaItemRepository.search("Java");
        
        // Assert
        assertEquals(2, results.size(), "Should find 2 items containing 'Java'");
        assertTrue(results.stream().anyMatch(i -> i.getTitle().contains("Java Programming")));
        assertTrue(results.stream().anyMatch(i -> i.getTitle().contains("JavaScript")));
    }
    
    @Test
    @Order(6)
    @DisplayName("Should search items by author")
    void testSearch_ByAuthor_ReturnsMatchingItems() {
        // Arrange
        createAndSaveMediaItem("Book A", "Martin Fowler", "BOOK", "ISBN-1");
        createAndSaveMediaItem("Book B", "Robert Martin", "BOOK", "ISBN-2");
        createAndSaveMediaItem("Book C", "Kent Beck", "BOOK", "ISBN-3");
        
        // Act
        List<MediaItem> results = mediaItemRepository.search("Martin");
        
        // Assert
        assertEquals(2, results.size(), "Should find 2 items with 'Martin' in author");
    }
    
    @Test
    @Order(7)
    @DisplayName("Should find items by type")
    void testFindByType_ReturnsItemsOfType() {
        // Arrange
        createAndSaveMediaItem("Book 1", "Author 1", "BOOK", "ISBN-1");
        createAndSaveMediaItem("Book 2", "Author 2", "BOOK", "ISBN-2");
        createAndSaveMediaItem("CD 1", "Artist 1", "CD", "CD-1");
        createAndSaveMediaItem("DVD 1", "Director 1", "DVD", "DVD-1");
        
        // Act
        List<MediaItem> books = mediaItemRepository.findByType("BOOK");
        List<MediaItem> cds = mediaItemRepository.findByType("CD");
        
        // Assert
        assertEquals(2, books.size(), "Should find 2 books");
        assertEquals(1, cds.size(), "Should find 1 CD");
    }
    
    @Test
    @Order(8)
    @DisplayName("Should update existing media item")
    void testUpdate_ExistingItem_UpdatesSuccessfully() {
        // Arrange
        MediaItem item = createAndSaveMediaItem("Old Title", "Old Author", "BOOK", "OLD-ISBN");
        Integer itemId = item.getItemId();
        
        // Act
        item.setTitle("New Title");
        item.setAuthor("New Author");
        item.setAvailableCopies(5);
        mediaItemRepository.update(item);
        
        // Assert
        Optional<MediaItem> updated = mediaItemRepository.findById(itemId);
        assertTrue(updated.isPresent(), "Updated item should exist");
        assertEquals("New Title", updated.get().getTitle());
        assertEquals("New Author", updated.get().getAuthor());
        assertEquals(5, updated.get().getAvailableCopies());
    }
    
    @Test
    @Order(9)
    @DisplayName("Should delete media item by ID")
    void testDelete_ExistingItem_DeletesSuccessfully() {
        // Arrange
        MediaItem item = createAndSaveMediaItem("To Be Deleted", "Author", "BOOK", "DELETE-ISBN");
        Integer itemId = item.getItemId();
        
        // Act
        mediaItemRepository.deleteById(itemId);
        
        // Assert
        Optional<MediaItem> deleted = mediaItemRepository.findById(itemId);
        assertFalse(deleted.isPresent(), "Deleted item should not exist");
    }
    
    @Test
    @Order(10)
    @DisplayName("Should find available items")
    void testFindAvailableItems_ReturnsOnlyAvailable() {
        // Arrange
        MediaItem available1 = createMediaItem("Available Book 1", "Author", "BOOK", "ISBN-1");
        available1.setTotalCopies(5);
        available1.setAvailableCopies(3);
        mediaItemRepository.save(available1);
        
        MediaItem available2 = createMediaItem("Available Book 2", "Author", "BOOK", "ISBN-2");
        available2.setTotalCopies(2);
        available2.setAvailableCopies(1);
        mediaItemRepository.save(available2);
        
        MediaItem unavailable = createMediaItem("Unavailable Book", "Author", "BOOK", "ISBN-3");
        unavailable.setTotalCopies(1);
        unavailable.setAvailableCopies(0);
        mediaItemRepository.save(unavailable);
        
        // Act
        List<MediaItem> availableItems = mediaItemRepository.findAvailableItems();
        
        // Assert
        assertEquals(2, availableItems.size(), "Should return only items with available copies > 0");
        assertTrue(availableItems.stream().allMatch(i -> i.getAvailableCopies() > 0));
    }
    
    @Test
    @Order(11)
    @DisplayName("Should update available copies")
    void testUpdateAvailableCopies_UpdatesCorrectly() {
        // Arrange
        MediaItem item = createAndSaveMediaItem("Test Book", "Author", "BOOK", "ISBN-TEST");
        Integer itemId = item.getItemId();
        
        // Act
        mediaItemRepository.updateAvailableCopies(itemId, 7);
        
        // Assert
        Optional<MediaItem> updated = mediaItemRepository.findById(itemId);
        assertTrue(updated.isPresent(), "Item should exist");
        assertEquals(7, updated.get().getAvailableCopies(), "Available copies should be updated");
    }
    
    @Test
    @Order(12)
    @DisplayName("Should find item by ISBN")
    void testFindByIsbn_ExistingISBN_ReturnsItem() {
        // Arrange
        createAndSaveMediaItem("Book with ISBN", "Author", "BOOK", "978-1234567890");
        
        // Act
        Optional<MediaItem> found = mediaItemRepository.findByIsbn("978-1234567890");
        
        // Assert
        assertTrue(found.isPresent(), "Item should be found by ISBN");
        assertEquals("Book with ISBN", found.get().getTitle());
    }
    
    @Test
    @Order(13)
    @DisplayName("Should return empty when ISBN does not exist")
    void testFindByIsbn_NonExistentISBN_ReturnsEmpty() {
        // Act
        Optional<MediaItem> found = mediaItemRepository.findByIsbn("NONEXISTENT-ISBN");
        
        // Assert
        assertFalse(found.isPresent(), "Should return empty for non-existent ISBN");
    }
    
    @Test
    @Order(14)
    @DisplayName("Should handle items with different late fee rates")
    void testSave_DifferentLateFees_SavesCorrectly() {
        // Arrange
        MediaItem book = createMediaItem("Book", "Author", "BOOK", "BOOK-ISBN");
        book.setLateFeesPerDay(new BigDecimal("10.00"));
        
        MediaItem cd = createMediaItem("CD Album", "Artist", "CD", "CD-ISBN");
        cd.setLateFeesPerDay(new BigDecimal("20.00"));
        
        // Act
        mediaItemRepository.save(book);
        mediaItemRepository.save(cd);
        
        // Assert
        Optional<MediaItem> savedBook = mediaItemRepository.findById(book.getItemId());
        Optional<MediaItem> savedCd = mediaItemRepository.findById(cd.getItemId());
        
        assertTrue(savedBook.isPresent());
        assertTrue(savedCd.isPresent());
        assertEquals(0, new BigDecimal("10.00").compareTo(savedBook.get().getLateFeesPerDay()));
        assertEquals(0, new BigDecimal("20.00").compareTo(savedCd.get().getLateFeesPerDay()));
    }
    
    // Helper methods
    private MediaItem createMediaItem(String title, String author, String type, String isbn) {
        MediaItem item = new MediaItem();
        item.setTitle(title);
        item.setAuthor(author);
        item.setType(type);
        item.setIsbn(isbn);
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Test Publisher");
        item.setTotalCopies(10);
        item.setAvailableCopies(10);
        item.setLateFeesPerDay(new BigDecimal("10.00"));
        return item;
    }
    
    private MediaItem createAndSaveMediaItem(String title, String author, String type, String isbn) {
        MediaItem item = createMediaItem(title, author, type, isbn);
        mediaItemRepository.save(item);
        return item;
    }
}
