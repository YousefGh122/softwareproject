package com.example.library.service;

import com.example.library.domain.Fine;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for payment operations.
 * Handles fine payments and outstanding balance queries.
 */
public interface PaymentService {
    
    /**
     * Retrieves all unpaid fines for a specific user.
     * 
     * @param userId the ID of the user
     * @return list of unpaid fines
     */
    List<Fine> getUnpaidFines(int userId);
    
    /**
     * Calculates the total amount of unpaid fines for a user.
     * 
     * @param userId the ID of the user
     * @return total unpaid amount
     */
    BigDecimal getTotalUnpaid(int userId);
    
    /**
     * Pays all outstanding fines for a user.
     * Marks all unpaid fines as paid with the current date.
     * 
     * @param userId the ID of the user
     */
    void payAllFinesForUser(int userId);
    
    /**
     * Pays a specific fine.
     * Marks the fine as paid with the current date.
     * 
     * @param fineId the ID of the fine to pay
     * @throws BusinessException if fine not found
     */
    void payFine(int fineId);
}
