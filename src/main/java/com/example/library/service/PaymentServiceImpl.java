package com.example.library.service;

import com.example.library.domain.Fine;
import com.example.library.repository.FineRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the payment service.
 * Provides fine payment processing and balance tracking functionality.
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final FineRepository fineRepository;
    
    /**
     * Constructs a new payment service with the specified fine repository.
     * 
     * @param fineRepository the repository for accessing fine data
     */
    public PaymentServiceImpl(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }
    
    /**
     * Retrieves all unpaid fines for a specific user.
     * Delegates to the repository to fetch unpaid fines through loan relationship.
     * 
     * @param userId the ID of the user
     * @return list of unpaid fines
     */
    @Override
    public List<Fine> getUnpaidFines(int userId) {
        return fineRepository.findUnpaidByUserId(userId);
    }
    
    /**
     * Calculates the total amount of unpaid fines for a user.
     * Delegates to the repository for sum calculation.
     * 
     * @param userId the ID of the user
     * @return total unpaid amount
     */
    @Override
    public BigDecimal getTotalUnpaid(int userId) {
        return fineRepository.calculateTotalUnpaidByUserId(userId);
    }
    
    /**
     * Pays all outstanding fines for a user.
     * Retrieves all unpaid fines and marks each as paid with the current date.
     * 
     * @param userId the ID of the user
     */
    @Override
    public void payAllFinesForUser(int userId) {
        List<Fine> unpaidFines = fineRepository.findUnpaidByUserId(userId);
        
        if (unpaidFines.isEmpty()) {
            return; // No fines to pay
        }
        
        LocalDate paymentDate = LocalDate.now();
        
        for (Fine fine : unpaidFines) {
            fineRepository.markAsPaid(fine.getFineId(), paymentDate);
        }
    }
    
    /**
     * Pays a specific fine.
     * Marks the fine as paid with the current date.
     * 
     * @param fineId the ID of the fine to pay
     * @throws BusinessException if fine not found
     */
    @Override
    public void payFine(int fineId) {
        // Verify fine exists before attempting to pay
        Optional<Fine> fineOptional = fineRepository.findById(fineId);
        
        if (!fineOptional.isPresent()) {
            throw new BusinessException("Fine not found with ID: " + fineId);
        }
        
        Fine fine = fineOptional.get();
        
        // Check if already paid
        if ("PAID".equalsIgnoreCase(fine.getStatus())) {
            throw new BusinessException("Fine has already been paid");
        }
        
        LocalDate paymentDate = LocalDate.now();
        fineRepository.markAsPaid(fineId, paymentDate);
    }
}
