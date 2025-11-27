package com.example.library.service;

import com.example.library.domain.Fine;
import com.example.library.repository.FineRepository;
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

class PaymentServiceImplTest {
    
    @Mock
    private FineRepository fineRepository;
    
    private PaymentServiceImpl paymentService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(fineRepository);
    }
    
    @Test
    void testPayFine_Success() {
        // Arrange
        Fine fine = new Fine();
        fine.setFineId(1);
        fine.setLoanId(100);
        fine.setAmount(new BigDecimal("15.00"));
        fine.setStatus("UNPAID");
        fine.setIssuedDate(LocalDate.now());
        
        when(fineRepository.findById(1)).thenReturn(Optional.of(fine));
        doNothing().when(fineRepository).markAsPaid(eq(1), any(LocalDate.class));
        
        // Act
        paymentService.payFine(1);
        
        // Assert
        verify(fineRepository, times(1)).findById(1);
        verify(fineRepository, times(1)).markAsPaid(eq(1), any(LocalDate.class));
    }
    
    @Test
    void testPayFine_AlreadyPaid() {
        // Arrange
        Fine fine = new Fine();
        fine.setFineId(1);
        fine.setLoanId(100);
        fine.setAmount(new BigDecimal("15.00"));
        fine.setStatus("PAID");
        fine.setPaidDate(LocalDate.now().minusDays(1));
        
        when(fineRepository.findById(1)).thenReturn(Optional.of(fine));
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            paymentService.payFine(1);
        });
        
        verify(fineRepository, never()).markAsPaid(anyInt(), any(LocalDate.class));
    }
    
    @Test
    void testPayFine_NotFound() {
        // Arrange
        when(fineRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            paymentService.payFine(999);
        });
        
        verify(fineRepository, never()).markAsPaid(anyInt(), any(LocalDate.class));
    }
    
    @Test
    void testGetUnpaidFines() {
        // Arrange
        int userId = 10;
        Fine fine1 = createFine(1, 100, "10.00", "UNPAID");
        Fine fine2 = createFine(2, 101, "5.50", "UNPAID");
        
        when(fineRepository.findUnpaidByUserId(userId)).thenReturn(Arrays.asList(fine1, fine2));
        
        // Act
        List<Fine> result = paymentService.getUnpaidFines(userId);
        
        // Assert
        assertEquals(2, result.size(), "Should return 2 unpaid fines");
        verify(fineRepository, times(1)).findUnpaidByUserId(userId);
    }
    
    @Test
    void testGetUnpaidFines_NoFines() {
        // Arrange
        int userId = 10;
        when(fineRepository.findUnpaidByUserId(userId)).thenReturn(Arrays.asList());
        
        // Act
        List<Fine> result = paymentService.getUnpaidFines(userId);
        
        // Assert
        assertTrue(result.isEmpty(), "Should return empty list when no fines");
        verify(fineRepository, times(1)).findUnpaidByUserId(userId);
    }
    
    @Test
    void testGetTotalUnpaid() {
        // Arrange
        int userId = 10;
        
        when(fineRepository.calculateTotalUnpaidByUserId(userId)).thenReturn(new BigDecimal("22.75"));
        
        // Act
        BigDecimal total = paymentService.getTotalUnpaid(userId);
        
        // Assert
        assertEquals(new BigDecimal("22.75"), total, "Total should be 22.75");
        verify(fineRepository, times(1)).calculateTotalUnpaidByUserId(userId);
    }
    
    @Test
    void testGetTotalUnpaid_NoFines() {
        // Arrange
        int userId = 10;
        when(fineRepository.calculateTotalUnpaidByUserId(userId)).thenReturn(BigDecimal.ZERO);
        
        // Act
        BigDecimal total = paymentService.getTotalUnpaid(userId);
        
        // Assert
        assertEquals(BigDecimal.ZERO, total, "Total should be 0 when no fines");
        verify(fineRepository, times(1)).calculateTotalUnpaidByUserId(userId);
    }
    
    @Test
    void testPayAllFinesForUser() {
        // Arrange
        int userId = 10;
        Fine fine1 = createFine(1, 100, "10.00", "UNPAID");
        Fine fine2 = createFine(2, 101, "5.50", "UNPAID");
        
        when(fineRepository.findUnpaidByUserId(userId)).thenReturn(Arrays.asList(fine1, fine2));
        doNothing().when(fineRepository).markAsPaid(anyInt(), any(LocalDate.class));
        
        // Act
        paymentService.payAllFinesForUser(userId);
        
        // Assert
        verify(fineRepository, times(1)).findUnpaidByUserId(userId);
        verify(fineRepository, times(2)).markAsPaid(anyInt(), any(LocalDate.class));
    }
    
    @Test
    void testPayAllFinesForUser_NoFines() {
        // Arrange
        int userId = 10;
        when(fineRepository.findUnpaidByUserId(userId)).thenReturn(Arrays.asList());
        
        // Act
        paymentService.payAllFinesForUser(userId);
        
        // Assert
        verify(fineRepository, times(1)).findUnpaidByUserId(userId);
        verify(fineRepository, never()).markAsPaid(anyInt(), any(LocalDate.class));
    }
    
    // Helper method
    private Fine createFine(Integer fineId, Integer loanId, String amount, String status) {
        Fine fine = new Fine();
        fine.setFineId(fineId);
        fine.setLoanId(loanId);
        fine.setAmount(new BigDecimal(amount));
        fine.setStatus(status);
        fine.setIssuedDate(LocalDate.now());
        return fine;
    }
}
