package com.example.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class FineExtendedTest {
    
    @Test
    void testDefaultConstructor() {
        Fine fine = new Fine();
        assertNotNull(fine);
    }
    
    @Test
    void testSettersAndGetters() {
        Fine fine = new Fine();
        LocalDate issueDate = LocalDate.now();
        LocalDate paidDate = LocalDate.now().plusDays(5);
        
        fine.setId(1L);
        fine.setLoanId(100L);
        fine.setAmount(15.50);
        fine.setIssueDate(issueDate);
        fine.setPaidDate(paidDate);
        fine.setPaid(true);
        
        assertEquals(1L, fine.getId());
        assertEquals(100L, fine.getLoanId());
        assertEquals(15.50, fine.getAmount(), 0.01);
        assertEquals(issueDate, fine.getIssueDate());
        assertEquals(paidDate, fine.getPaidDate());
        assertTrue(fine.isPaid());
    }
    
    @Test
    void testIsPaid_True() {
        Fine fine = new Fine();
        fine.setPaid(true);
        
        assertTrue(fine.isPaid());
    }
    
    @Test
    void testIsPaid_False() {
        Fine fine = new Fine();
        fine.setPaid(false);
        
        assertFalse(fine.isPaid());
    }
    
    @Test
    void testToString_ContainsAmount() {
        Fine fine = new Fine();
        fine.setAmount(10.00);
        fine.setLoanId(5L);
        
        String str = fine.toString();
        assertTrue(str.contains("10") || str.contains("5"));
    }
    
    @Test
    void testAmountCalculation() {
        Fine fine = new Fine();
        fine.setAmount(5.00);
        
        // Test doubling the fine
        fine.setAmount(fine.getAmount() * 2);
        assertEquals(10.00, fine.getAmount(), 0.01);
    }
    
    @Test
    void testPaidDateWhenUnpaid() {
        Fine fine = new Fine();
        fine.setPaid(false);
        fine.setPaidDate(null);
        
        assertNull(fine.getPaidDate());
        assertFalse(fine.isPaid());
    }
    
    @Test
    void testPaidDateWhenPaid() {
        Fine fine = new Fine();
        LocalDate paidDate = LocalDate.now();
        
        fine.setPaid(true);
        fine.setPaidDate(paidDate);
        
        assertEquals(paidDate, fine.getPaidDate());
        assertTrue(fine.isPaid());
    }
    
    @Test
    void testFineAmountPrecision() {
        Fine fine = new Fine();
        fine.setAmount(12.345);
        
        assertEquals(12.345, fine.getAmount(), 0.001);
    }
    
    @Test
    void testIssueDateBeforePaidDate() {
        Fine fine = new Fine();
        LocalDate issueDate = LocalDate.now().minusDays(10);
        LocalDate paidDate = LocalDate.now();
        
        fine.setIssueDate(issueDate);
        fine.setPaidDate(paidDate);
        fine.setPaid(true);
        
        assertTrue(fine.getIssueDate().isBefore(fine.getPaidDate()));
    }
}
