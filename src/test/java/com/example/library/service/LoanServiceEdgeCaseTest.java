package com.example.library.service;

import com.example.library.domain.Loan;
import com.example.library.domain.MediaItem;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.MediaItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoanServiceEdgeCaseTest {
    
    @Mock
    private LoanRepository loanRepository;
    
    @Mock
    private MediaItemRepository mediaItemRepository;
    
    private LoanService loanService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanService = new LoanService(loanRepository, mediaItemRepository);
    }
    
    @Test
    void testCheckoutItem_MediaItemNotFound() {
        when(mediaItemRepository.findById(999)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            loanService.checkoutItem(1, 999);
        });
        
        verify(mediaItemRepository).findById(999);
        verify(loanRepository, never()).save(any());
    }
    
    @Test
    void testCheckoutItem_NoAvailableCopies() {
        MediaItem item = new MediaItem();
        item.setItemId(1);
        item.setTitle("Test Book");
        item.setAvailableCopies(0);
        item.setTotalCopies(1);
        
        when(mediaItemRepository.findById(1)).thenReturn(Optional.of(item));
        
        assertThrows(RuntimeException.class, () -> {
            loanService.checkoutItem(1, 1);
        });
        
        verify(mediaItemRepository).findById(1);
        verify(loanRepository, never()).save(any());
    }
    
    @Test
    void testCheckoutItem_Success() {
        MediaItem item = new MediaItem();
        item.setItemId(1);
        item.setTitle("Test Book");
        item.setAvailableCopies(5);
        item.setTotalCopies(10);
        
        Loan savedLoan = new Loan();
        savedLoan.setLoanId(1);
        savedLoan.setUserId(1);
        savedLoan.setItemId(1);
        savedLoan.setStatus("ACTIVE");
        
        when(mediaItemRepository.findById(1)).thenReturn(Optional.of(item));
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);
        when(mediaItemRepository.update(any(MediaItem.class))).thenReturn(item);
        
        Loan result = loanService.checkoutItem(1, 1);
        
        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(mediaItemRepository).update(argThat(mi -> mi.getAvailableCopies() == 4));
    }
    
    @Test
    void testReturnItem_LoanNotFound() {
        when(loanRepository.findById(999)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            loanService.returnItem(999);
        });
        
        verify(loanRepository).findById(999);
        verify(mediaItemRepository, never()).update(any());
    }
    
    @Test
    void testReturnItem_AlreadyReturned() {
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setItemId(1);
        loan.setStatus("RETURNED");
        loan.setReturnDate(LocalDate.now().minusDays(1));
        
        when(loanRepository.findById(1)).thenReturn(Optional.of(loan));
        
        assertThrows(RuntimeException.class, () -> {
            loanService.returnItem(1);
        });
        
        verify(loanRepository).findById(1);
        verify(loanRepository, never()).update(any());
    }
    
    @Test
    void testReturnItem_Success() {
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setItemId(1);
        loan.setUserId(1);
        loan.setStatus("ACTIVE");
        loan.setLoanDate(LocalDate.now().minusDays(7));
        loan.setDueDate(LocalDate.now().plusDays(7));
        
        MediaItem item = new MediaItem();
        item.setItemId(1);
        item.setAvailableCopies(5);
        item.setTotalCopies(10);
        
        when(loanRepository.findById(1)).thenReturn(Optional.of(loan));
        when(mediaItemRepository.findById(1)).thenReturn(Optional.of(item));
        when(loanRepository.update(any(Loan.class))).thenReturn(loan);
        when(mediaItemRepository.update(any(MediaItem.class))).thenReturn(item);
        
        Loan result = loanService.returnItem(1);
        
        assertNotNull(result);
        assertEquals("RETURNED", result.getStatus());
        assertNotNull(result.getReturnDate());
        verify(mediaItemRepository).update(argThat(mi -> mi.getAvailableCopies() == 6));
    }
    
    @Test
    void testGetOverdueLoans() {
        assertDoesNotThrow(() -> {
            loanService.getOverdueLoans();
        });
        
        verify(loanRepository).findOverdueLoans();
    }
    
    @Test
    void testGetLoansByUserId() {
        assertDoesNotThrow(() -> {
            loanService.getLoansByUserId(1);
        });
        
        verify(loanRepository).findByUserId(1);
    }
}
