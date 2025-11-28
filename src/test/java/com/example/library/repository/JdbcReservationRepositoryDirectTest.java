package com.example.library.repository;

import com.example.library.DatabaseConnection;
import com.example.library.domain.MediaItem;
import com.example.library.domain.Reservation;
import com.example.library.domain.User;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct database tests for JdbcReservationRepository.
 * Tests against actual PostgreSQL database (not Testcontainers).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcReservationRepositoryDirectTest {
    
    private static ReservationRepository reservationRepository;
    private static UserRepository userRepository;
    private static MediaItemRepository mediaItemRepository;
    
    @BeforeAll
    static void setup() {
        reservationRepository = new JdbcReservationRepository();
        userRepository = new JdbcUserRepository();
        mediaItemRepository = new JdbcMediaItemRepository();
    }
    
    @BeforeEach
    void cleanTables() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            // Clean in correct order due to foreign keys
            stmt.execute("DELETE FROM reservation");
            stmt.execute("DELETE FROM fine");
            stmt.execute("DELETE FROM loan");
            stmt.execute("DELETE FROM media_item");
            stmt.execute("DELETE FROM app_user");
        } catch (SQLException e) {
            System.err.println("Warning: Could not clean tables - " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Should save and retrieve reservation")
    void testSaveAndFindById() {
        // Arrange
        User user = createAndSaveUser("testuser1", "test1@example.com");
        MediaItem item = createAndSaveMediaItem("Test Book 1", "Test Author", 2, 0);
        
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getUserId());
        reservation.setItemId(item.getItemId());
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setExpiryDate(LocalDateTime.now().plusHours(48));
        reservation.setStatus("ACTIVE");
        
        // Act
        Reservation saved = reservationRepository.save(reservation);
        Optional<Reservation> found = reservationRepository.findById(saved.getReservationId());
        
        // Assert
        assertNotNull(saved.getReservationId());
        assertTrue(found.isPresent());
        assertEquals(user.getUserId(), found.get().getUserId());
        assertEquals(item.getItemId(), found.get().getItemId());
        assertEquals("ACTIVE", found.get().getStatus());
        System.out.println("✓ Test 1 passed: Save and retrieve reservation");
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find reservations by user ID")
    void testFindByUserId() {
        // Arrange
        User user = createAndSaveUser("testuser2", "test2@example.com");
        MediaItem item1 = createAndSaveMediaItem("Book 1", "Author 1", 2, 0);
        MediaItem item2 = createAndSaveMediaItem("Book 2", "Author 2", 2, 0);
        
        createAndSaveReservation(user.getUserId(), item1.getItemId());
        createAndSaveReservation(user.getUserId(), item2.getItemId());
        
        // Act
        List<Reservation> reservations = reservationRepository.findByUserId(user.getUserId());
        
        // Assert
        assertEquals(2, reservations.size());
        System.out.println("✓ Test 2 passed: Find reservations by user ID");
    }
    
    @Test
    @Order(3)
    @DisplayName("Should find active reservations by item ID in FIFO order")
    void testFindActiveByItemIdOrder() throws InterruptedException {
        // Arrange
        User user1 = createAndSaveUser("user1", "user1@example.com");
        User user2 = createAndSaveUser("user2", "user2@example.com");
        User user3 = createAndSaveUser("user3", "user3@example.com");
        MediaItem item = createAndSaveMediaItem("Popular Book", "Author", 2, 0);
        
        // Create reservations with slight delays to ensure ordering
        Reservation res1 = createAndSaveReservation(user1.getUserId(), item.getItemId());
        Thread.sleep(100);
        Reservation res2 = createAndSaveReservation(user2.getUserId(), item.getItemId());
        Thread.sleep(100);
        Reservation res3 = createAndSaveReservation(user3.getUserId(), item.getItemId());
        
        // Act
        List<Reservation> queue = reservationRepository.findActiveByItemId(item.getItemId());
        
        // Assert
        assertEquals(3, queue.size());
        assertEquals(res1.getReservationId(), queue.get(0).getReservationId(), "First reservation should be first in queue");
        assertEquals(res2.getReservationId(), queue.get(1).getReservationId(), "Second reservation should be second in queue");
        assertEquals(res3.getReservationId(), queue.get(2).getReservationId(), "Third reservation should be third in queue");
        System.out.println("✓ Test 3 passed: FIFO queue ordering works correctly");
    }
    
    @Test
    @Order(4)
    @DisplayName("Should update reservation status")
    void testUpdateReservation() {
        // Arrange
        User user = createAndSaveUser("testuser4", "test4@example.com");
        MediaItem item = createAndSaveMediaItem("Book 4", "Author 4", 2, 0);
        Reservation reservation = createAndSaveReservation(user.getUserId(), item.getItemId());
        
        // Act
        reservation.setStatus("FULFILLED");
        reservationRepository.update(reservation);
        Optional<Reservation> updated = reservationRepository.findById(reservation.getReservationId());
        
        // Assert
        assertTrue(updated.isPresent());
        assertEquals("FULFILLED", updated.get().getStatus());
        System.out.println("✓ Test 4 passed: Update reservation status");
    }
    
    @Test
    @Order(5)
    @DisplayName("Should count active reservations")
    void testCountActiveByItemId() {
        // Arrange
        User user1 = createAndSaveUser("user5a", "user5a@example.com");
        User user2 = createAndSaveUser("user5b", "user5b@example.com");
        MediaItem item = createAndSaveMediaItem("Book 5", "Author 5", 2, 0);
        
        createAndSaveReservation(user1.getUserId(), item.getItemId());
        createAndSaveReservation(user2.getUserId(), item.getItemId());
        
        // Act
        int count = reservationRepository.countActiveByItemId(item.getItemId());
        
        // Assert
        assertEquals(2, count);
        System.out.println("✓ Test 5 passed: Count active reservations");
    }
    
    @Test
    @Order(6)
    @DisplayName("Should find expired reservations")
    void testFindExpiredReservations() {
        // Arrange
        User user = createAndSaveUser("testuser6", "test6@example.com");
        MediaItem item = createAndSaveMediaItem("Book 6", "Author 6", 2, 0);
        
        Reservation expiredRes = new Reservation();
        expiredRes.setUserId(user.getUserId());
        expiredRes.setItemId(item.getItemId());
        expiredRes.setReservationDate(LocalDateTime.now().minusDays(3));
        expiredRes.setExpiryDate(LocalDateTime.now().minusHours(1)); // Expired 1 hour ago
        expiredRes.setStatus("ACTIVE");
        reservationRepository.save(expiredRes);
        
        // Act
        List<Reservation> expired = reservationRepository.findExpiredReservations(LocalDateTime.now());
        
        // Assert
        assertFalse(expired.isEmpty());
        System.out.println("✓ Test 6 passed: Find expired reservations");
    }
    
    @Test
    @Order(7)
    @DisplayName("Should find all reservations by item ID")
    void testFindByItemId() {
        // Arrange
        User user1 = createAndSaveUser("user7a", "user7a@example.com");
        User user2 = createAndSaveUser("user7b", "user7b@example.com");
        MediaItem item = createAndSaveMediaItem("Book 7", "Author 7", 2, 0);
        
        createAndSaveReservation(user1.getUserId(), item.getItemId());
        createAndSaveReservation(user2.getUserId(), item.getItemId());
        
        // Act
        List<Reservation> reservations = reservationRepository.findByItemId(item.getItemId());
        
        // Assert
        assertEquals(2, reservations.size());
        System.out.println("✓ Test 7 passed: Find all reservations by item ID");
    }
    
    @Test
    @Order(8)
    @DisplayName("Should find active reservations by user ID")
    void testFindActiveByUserId() {
        // Arrange
        User user = createAndSaveUser("user8", "user8@example.com");
        MediaItem item1 = createAndSaveMediaItem("Book 8a", "Author 8a", 2, 0);
        MediaItem item2 = createAndSaveMediaItem("Book 8b", "Author 8b", 2, 0);
        MediaItem item3 = createAndSaveMediaItem("Book 8c", "Author 8c", 2, 0);
        
        // Create active and cancelled reservations
        createAndSaveReservation(user.getUserId(), item1.getItemId());
        createAndSaveReservation(user.getUserId(), item2.getItemId());
        
        Reservation cancelled = createAndSaveReservation(user.getUserId(), item3.getItemId());
        cancelled.setStatus("CANCELLED");
        reservationRepository.update(cancelled);
        
        // Act
        List<Reservation> activeReservations = reservationRepository.findActiveByUserId(user.getUserId());
        
        // Assert
        assertEquals(2, activeReservations.size());
        assertTrue(activeReservations.stream().allMatch(r -> "ACTIVE".equals(r.getStatus())));
        System.out.println("✓ Test 8 passed: Find active reservations by user ID");
    }
    
    @Test
    @Order(9)
    @DisplayName("Should find all reservations")
    void testFindAll() {
        // Arrange
        User user1 = createAndSaveUser("user9a", "user9a@example.com");
        User user2 = createAndSaveUser("user9b", "user9b@example.com");
        MediaItem item1 = createAndSaveMediaItem("Book 9a", "Author 9a", 2, 0);
        MediaItem item2 = createAndSaveMediaItem("Book 9b", "Author 9b", 2, 0);
        
        createAndSaveReservation(user1.getUserId(), item1.getItemId());
        createAndSaveReservation(user2.getUserId(), item2.getItemId());
        
        // Act
        List<Reservation> allReservations = reservationRepository.findAll();
        
        // Assert
        assertTrue(allReservations.size() >= 2);
        System.out.println("✓ Test 9 passed: Find all reservations");
    }
    
    @Test
    @Order(10)
    @DisplayName("Should delete reservation by ID")
    void testDeleteById() {
        // Arrange
        User user = createAndSaveUser("user10", "user10@example.com");
        MediaItem item = createAndSaveMediaItem("Book 10", "Author 10", 2, 0);
        Reservation reservation = createAndSaveReservation(user.getUserId(), item.getItemId());
        Integer reservationId = reservation.getReservationId();
        
        // Act
        reservationRepository.deleteById(reservationId);
        Optional<Reservation> deleted = reservationRepository.findById(reservationId);
        
        // Assert
        assertFalse(deleted.isPresent());
        System.out.println("✓ Test 10 passed: Delete reservation by ID");
    }
    
    // Helper methods
    
    private User createAndSaveUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password123");
        user.setEmail(email);
        user.setRole("MEMBER");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    private MediaItem createAndSaveMediaItem(String title, String author, int totalCopies, int availableCopies) {
        MediaItem item = new MediaItem();
        item.setTitle(title);
        item.setAuthor(author);
        item.setType("BOOK");
        item.setIsbn("ISBN-" + System.currentTimeMillis());
        item.setPublicationDate(LocalDate.now().minusYears(1));
        item.setPublisher("Test Publisher");
        item.setTotalCopies(totalCopies);
        item.setAvailableCopies(availableCopies);
        item.setLateFeesPerDay(new BigDecimal("10.00"));
        return mediaItemRepository.save(item);
    }
    
    private Reservation createAndSaveReservation(Integer userId, Integer itemId) {
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setItemId(itemId);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setExpiryDate(LocalDateTime.now().plusHours(48));
        reservation.setStatus("ACTIVE");
        return reservationRepository.save(reservation);
    }
}
