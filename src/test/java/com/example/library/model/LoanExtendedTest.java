package com.example.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class LoanExtendedTest {
    
    @Test
    void testIsOverdue_NotOverdue() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().plusDays(5));
        loan.setReturnDate(null);
        
        assertFalse(loan.isOverdue());
    }
    
    @Test
    void testIsOverdue_Overdue() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().minusDays(1));
        loan.setReturnDate(null);
        
        assertTrue(loan.isOverdue());
    }
    
    @Test
    void testIsOverdue_Returned() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().minusDays(1));
        loan.setReturnDate(LocalDate.now());
        
        assertFalse(loan.isOverdue());
    }
    
    @Test
    void testIsOverdue_DueToday() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now());
        loan.setReturnDate(null);
        
        assertFalse(loan.isOverdue());
    }
    
    @Test
    void testCalculateLateFee_NoLateFee() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().plusDays(5));
        loan.setReturnDate(null);
        
        assertEquals(0.0, loan.calculateLateFee(), 0.01);
    }
    
    @Test
    void testCalculateLateFee_OneDayLate() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().minusDays(1));
        loan.setReturnDate(null);
        
        assertEquals(1.0, loan.calculateLateFee(), 0.01);
    }
    
    @Test
    void testCalculateLateFee_MultipleDaysLate() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().minusDays(5));
        loan.setReturnDate(null);
        
        assertEquals(5.0, loan.calculateLateFee(), 0.01);
    }
    
    @Test
    void testCalculateLateFee_Returned() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().minusDays(5));
        loan.setReturnDate(LocalDate.now());
        
        assertEquals(0.0, loan.calculateLateFee(), 0.01);
    }
    
    @Test
    void testToString_ContainsAllFields() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUserId(100L);
        loan.setMediaItemId(200L);
        loan.setLoanDate(LocalDate.of(2024, 1, 1));
        loan.setDueDate(LocalDate.of(2024, 1, 15));
        
        String str = loan.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("100"));
        assertTrue(str.contains("200"));
    }
    
    @Test
    void testSettersAndGetters() {
        Loan loan = new Loan();
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = LocalDate.now().plusDays(14);
        LocalDate returnDate = LocalDate.now().plusDays(10);
        
        loan.setId(5L);
        loan.setUserId(50L);
        loan.setMediaItemId(100L);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setReturnDate(returnDate);
        
        assertEquals(5L, loan.getId());
        assertEquals(50L, loan.getUserId());
        assertEquals(100L, loan.getMediaItemId());
        assertEquals(loanDate, loan.getLoanDate());
        assertEquals(dueDate, loan.getDueDate());
        assertEquals(returnDate, loan.getReturnDate());
    }
}
