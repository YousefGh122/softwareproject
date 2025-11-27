package com.example.library.repository;

import com.example.library.domain.Fine;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FineRepository {
    
    /**
     * Save a new fine to the database
     * @param fine the fine to save
     * @return the saved fine with generated ID
     */
    Fine save(Fine fine);
    
    /**
     * Update an existing fine
     * @param fine the fine to update
     * @return the updated fine
     */
    Fine update(Fine fine);
    
    /**
     * Find a fine by ID
     * @param fineId the fine ID
     * @return Optional containing the fine if found
     */
    Optional<Fine> findById(Integer fineId);
    
    /**
     * Find a fine by loan ID
     * @param loanId the loan ID
     * @return Optional containing the fine if found
     */
    Optional<Fine> findByLoanId(Integer loanId);
    
    /**
     * Find all fines
     * @return list of all fines
     */
    List<Fine> findAll();
    
    /**
     * Find fines by status
     * @param status the fine status (e.g., UNPAID, PAID)
     * @return list of fines with the specified status
     */
    List<Fine> findByStatus(String status);
    
    /**
     * Find unpaid fines by user ID
     * @param userId the user ID
     * @return list of unpaid fines for the specified user
     */
    List<Fine> findUnpaidByUserId(Integer userId);
    
    /**
     * Find all fines by user ID (through loan relationship)
     * @param userId the user ID
     * @return list of all fines for the specified user
     */
    List<Fine> findByUserId(Integer userId);
    
    /**
     * Calculate total unpaid fines for a user
     * @param userId the user ID
     * @return total amount of unpaid fines
     */
    BigDecimal calculateTotalUnpaidByUserId(Integer userId);
    
    /**
     * Delete a fine by ID
     * @param fineId the fine ID
     * @return true if deleted successfully
     */
    boolean deleteById(Integer fineId);
    
    /**
     * Check if a fine exists for a loan
     * @param loanId the loan ID
     * @return true if a fine exists for the loan
     */
    boolean existsByLoanId(Integer loanId);
    
    /**
     * Mark a fine as paid
     * @param fineId the fine ID
     * @param paidDate the date the fine was paid
     */
    void markAsPaid(Integer fineId, LocalDate paidDate);
}
