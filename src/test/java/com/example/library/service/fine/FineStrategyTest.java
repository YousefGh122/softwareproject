package com.example.library.service.fine;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class FineStrategyTest {
    
    @Test
    void testBookFineStrategy_CalculatesFineCorrectly() {
        // Arrange
        FineStrategy strategy = new BookFineStrategy();
        
        // Act & Assert
        assertEquals(new BigDecimal("10.00"), strategy.calculateFine(1), "1 day overdue should be 10 NIS");
        assertEquals(new BigDecimal("30.00"), strategy.calculateFine(3), "3 days overdue should be 30 NIS");
        assertEquals(new BigDecimal("100.00"), strategy.calculateFine(10), "10 days overdue should be 100 NIS");
    }
    
    @Test
    void testBookFineStrategy_ZeroForNonOverdueDays() {
        // Arrange
        FineStrategy strategy = new BookFineStrategy();
        
        // Act & Assert
        assertEquals(BigDecimal.ZERO, strategy.calculateFine(0), "0 days should return zero");
        assertEquals(BigDecimal.ZERO, strategy.calculateFine(-1), "Negative days should return zero");
        assertEquals(BigDecimal.ZERO, strategy.calculateFine(-10), "Negative days should return zero");
    }
    
    @Test
    void testCDFineStrategy_CalculatesFineCorrectly() {
        // Arrange
        FineStrategy strategy = new CDFineStrategy();
        
        // Act & Assert
        assertEquals(new BigDecimal("20.00"), strategy.calculateFine(1), "1 day overdue should be 20 NIS");
        assertEquals(new BigDecimal("60.00"), strategy.calculateFine(3), "3 days overdue should be 60 NIS");
        assertEquals(new BigDecimal("200.00"), strategy.calculateFine(10), "10 days overdue should be 200 NIS");
    }
    
    @Test
    void testCDFineStrategy_ZeroForNonOverdueDays() {
        // Arrange
        FineStrategy strategy = new CDFineStrategy();
        
        // Act & Assert
        assertEquals(BigDecimal.ZERO, strategy.calculateFine(0), "0 days should return zero");
        assertEquals(BigDecimal.ZERO, strategy.calculateFine(-1), "Negative days should return zero");
        assertEquals(BigDecimal.ZERO, strategy.calculateFine(-5), "Negative days should return zero");
    }
    
    @Test
    void testBookFineStrategy_LargeNumberOfDays() {
        // Arrange
        FineStrategy strategy = new BookFineStrategy();
        
        // Act
        BigDecimal fine = strategy.calculateFine(365);
        
        // Assert
        assertEquals(new BigDecimal("3650.00"), fine, "365 days should be 3650 NIS");
    }
    
    @Test
    void testCDFineStrategy_LargeNumberOfDays() {
        // Arrange
        FineStrategy strategy = new CDFineStrategy();
        
        // Act
        BigDecimal fine = strategy.calculateFine(365);
        
        // Assert
        assertEquals(new BigDecimal("7300.00"), fine, "365 days should be 7300 NIS");
    }
}
