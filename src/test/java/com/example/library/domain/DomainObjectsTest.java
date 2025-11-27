package com.example.library.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DomainObjectsTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

        assertEquals(1, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("USER", user.getRole());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), user.getCreatedAt());
    }

    @Test
    void testUserToString() {
        User user = new User(1, "admin", "pass", "admin@example.com", "ADMIN", LocalDateTime.now());
        String toString = user.toString();
        assertTrue(toString.contains("admin"));
        assertTrue(toString.contains("ADMIN"));
        assertTrue(toString.contains("admin@example.com"));
    }

    @Test
    void testMediaItemGettersAndSetters() {
        MediaItem item = new MediaItem();
        item.setItemId(1);
        item.setTitle("Test Book");
        item.setAuthor("Test Author");
        item.setType("BOOK");
        item.setIsbn("978-1234567890");
        item.setPublicationDate(LocalDate.of(2020, 1, 1));
        item.setPublisher("Test Publisher");
        item.setTotalCopies(10);
        item.setAvailableCopies(8);
        item.setLateFeesPerDay(BigDecimal.valueOf(1.50));

        assertEquals(1, item.getItemId());
        assertEquals("Test Book", item.getTitle());
        assertEquals("Test Author", item.getAuthor());
        assertEquals("BOOK", item.getType());
        assertEquals("978-1234567890", item.getIsbn());
        assertEquals(LocalDate.of(2020, 1, 1), item.getPublicationDate());
        assertEquals("Test Publisher", item.getPublisher());
        assertEquals(10, item.getTotalCopies());
        assertEquals(8, item.getAvailableCopies());
        assertEquals(BigDecimal.valueOf(1.50), item.getLateFeesPerDay());
    }

    @Test
    void testMediaItemToString() {
        MediaItem item = new MediaItem(1, "Book Title", "Author Name", "BOOK", 
            "978-1234567890", LocalDate.of(2020, 1, 1), "Publisher", 5, 3, BigDecimal.valueOf(1.00));
        String toString = item.toString();
        assertTrue(toString.contains("Book Title"));
        assertTrue(toString.contains("Author Name"));
        assertTrue(toString.contains("BOOK"));
    }

    @Test
    void testLoanGettersAndSetters() {
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setUserId(5);
        loan.setItemId(10);
        loan.setLoanDate(LocalDate.of(2024, 1, 1));
        loan.setDueDate(LocalDate.of(2024, 1, 15));
        loan.setReturnDate(LocalDate.of(2024, 1, 10));
        loan.setStatus("RETURNED");

        assertEquals(1, loan.getLoanId());
        assertEquals(5, loan.getUserId());
        assertEquals(10, loan.getItemId());
        assertEquals(LocalDate.of(2024, 1, 1), loan.getLoanDate());
        assertEquals(LocalDate.of(2024, 1, 15), loan.getDueDate());
        assertEquals(LocalDate.of(2024, 1, 10), loan.getReturnDate());
        assertEquals("RETURNED", loan.getStatus());
    }

    @Test
    void testLoanToString() {
        Loan loan = new Loan(1, 5, 10, LocalDate.of(2024, 1, 1), 
            LocalDate.of(2024, 1, 15), null, "ACTIVE");
        String toString = loan.toString();
        assertTrue(toString.contains("loanId=1"));
        assertTrue(toString.contains("userId=5"));
        assertTrue(toString.contains("ACTIVE"));
    }

    @Test
    void testFineGettersAndSetters() {
        Fine fine = new Fine();
        fine.setFineId(1);
        fine.setLoanId(100);
        fine.setAmount(BigDecimal.valueOf(15.50));
        fine.setIssuedDate(LocalDate.of(2024, 1, 20));
        fine.setPaidDate(LocalDate.of(2024, 1, 25));
        fine.setStatus("PAID");

        assertEquals(1, fine.getFineId());
        assertEquals(100, fine.getLoanId());
        assertEquals(BigDecimal.valueOf(15.50), fine.getAmount());
        assertEquals(LocalDate.of(2024, 1, 20), fine.getIssuedDate());
        assertEquals(LocalDate.of(2024, 1, 25), fine.getPaidDate());
        assertEquals("PAID", fine.getStatus());
    }

    @Test
    void testFineToString() {
        Fine fine = new Fine(1, 100, BigDecimal.valueOf(10.00), 
            LocalDate.of(2024, 1, 20), "UNPAID", null);
        String toString = fine.toString();
        assertTrue(toString.contains("fineId=1"));
        assertTrue(toString.contains("loanId=100"));
        assertTrue(toString.contains("UNPAID"));
    }

    @Test
    void testUserConstructorWithAllFields() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        User user = new User(1, "john", "pass123", "john@example.com", "USER", createdAt);
        
        assertEquals(1, user.getUserId());
        assertEquals("john", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("USER", user.getRole());
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    void testMediaItemConstructorWithAllFields() {
        LocalDate pubDate = LocalDate.of(2020, 1, 1);
        BigDecimal fee = BigDecimal.valueOf(2.00);
        MediaItem item = new MediaItem(1, "Title", "Author", "BOOK", 
            "978-1234567890", pubDate, "Publisher", 10, 8, fee);
        
        assertEquals(1, item.getItemId());
        assertEquals("Title", item.getTitle());
        assertEquals("Author", item.getAuthor());
        assertEquals("BOOK", item.getType());
        assertEquals("978-1234567890", item.getIsbn());
        assertEquals(pubDate, item.getPublicationDate());
        assertEquals("Publisher", item.getPublisher());
        assertEquals(10, item.getTotalCopies());
        assertEquals(8, item.getAvailableCopies());
        assertEquals(fee, item.getLateFeesPerDay());
    }

    @Test
    void testLoanConstructorWithAllFields() {
        LocalDate loanDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 15);
        LocalDate returnDate = LocalDate.of(2024, 1, 10);
        Loan loan = new Loan(1, 5, 10, loanDate, dueDate, returnDate, "RETURNED");
        
        assertEquals(1, loan.getLoanId());
        assertEquals(5, loan.getUserId());
        assertEquals(10, loan.getItemId());
        assertEquals(loanDate, loan.getLoanDate());
        assertEquals(dueDate, loan.getDueDate());
        assertEquals(returnDate, loan.getReturnDate());
        assertEquals("RETURNED", loan.getStatus());
    }

    @Test
    void testFineConstructorWithAllFields() {
        LocalDate issued = LocalDate.of(2024, 1, 20);
        LocalDate paid = LocalDate.of(2024, 1, 25);
        BigDecimal amount = BigDecimal.valueOf(15.00);
        Fine fine = new Fine(1, 100, amount, issued, "PAID", paid);
        
        assertEquals(1, fine.getFineId());
        assertEquals(100, fine.getLoanId());
        assertEquals(amount, fine.getAmount());
        assertEquals(issued, fine.getIssuedDate());
        assertEquals(paid, fine.getPaidDate());
        assertEquals("PAID", fine.getStatus());
    }
}
