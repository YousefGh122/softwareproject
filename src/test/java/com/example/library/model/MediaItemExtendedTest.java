package com.example.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MediaItemExtendedTest {
    
    @Test
    void testConstructor() {
        MediaItem item = new MediaItem();
        assertNotNull(item);
    }
    
    @Test
    void testSettersAndGetters() {
        MediaItem item = new MediaItem();
        
        item.setId(1L);
        item.setTitle("Test Book");
        item.setAuthor("Test Author");
        item.setIsbn("1234567890");
        item.setPublicationYear(2024);
        item.setTotalCopies(5);
        item.setAvailableCopies(3);
        
        assertEquals(1L, item.getId());
        assertEquals("Test Book", item.getTitle());
        assertEquals("Test Author", item.getAuthor());
        assertEquals("1234567890", item.getIsbn());
        assertEquals(2024, item.getPublicationYear());
        assertEquals(5, item.getTotalCopies());
        assertEquals(3, item.getAvailableCopies());
    }
    
    @Test
    void testIsAvailable_True() {
        MediaItem item = new MediaItem();
        item.setAvailableCopies(1);
        
        assertTrue(item.isAvailable());
    }
    
    @Test
    void testIsAvailable_False() {
        MediaItem item = new MediaItem();
        item.setAvailableCopies(0);
        
        assertFalse(item.isAvailable());
    }
    
    @Test
    void testToString_ContainsTitle() {
        MediaItem item = new MediaItem();
        item.setTitle("Test Book");
        item.setAuthor("Test Author");
        
        String str = item.toString();
        assertTrue(str.contains("Test Book"));
    }
    
    @Test
    void testDecrementAvailableCopies() {
        MediaItem item = new MediaItem();
        item.setAvailableCopies(5);
        
        item.setAvailableCopies(item.getAvailableCopies() - 1);
        assertEquals(4, item.getAvailableCopies());
    }
    
    @Test
    void testIncrementAvailableCopies() {
        MediaItem item = new MediaItem();
        item.setAvailableCopies(3);
        
        item.setAvailableCopies(item.getAvailableCopies() + 1);
        assertEquals(4, item.getAvailableCopies());
    }
    
    @Test
    void testMultipleCopiesHandling() {
        MediaItem item = new MediaItem();
        item.setTotalCopies(10);
        item.setAvailableCopies(10);
        
        // Simulate 3 loans
        item.setAvailableCopies(item.getAvailableCopies() - 3);
        assertEquals(7, item.getAvailableCopies());
        assertTrue(item.isAvailable());
        
        // Return 1 book
        item.setAvailableCopies(item.getAvailableCopies() + 1);
        assertEquals(8, item.getAvailableCopies());
    }
}
