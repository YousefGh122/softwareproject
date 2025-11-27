package com.example.library.repository;

import com.example.library.domain.Loan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    
    /**
     * Save a new loan to the database
     * @param loan the loan to save
     * @return the saved loan with generated ID
     */
    Loan save(Loan loan);
    
    /**
     * Update an existing loan
     * @param loan the loan to update
     * @return the updated loan
     */
    Loan update(Loan loan);
    
    /**
     * Find a loan by ID
     * @param loanId the loan ID
     * @return Optional containing the loan if found
     */
    Optional<Loan> findById(Integer loanId);
    
    /**
     * Find all loans
     * @return list of all loans
     */
    List<Loan> findAll();
    
    /**
     * Find loans by user ID
     * @param userId the user ID
     * @return list of loans for the specified user
     */
    List<Loan> findByUserId(Integer userId);
    
    /**
     * Find loans by item ID
     * @param itemId the item ID
     * @return list of loans for the specified item
     */
    List<Loan> findByItemId(Integer itemId);
    
    /**
     * Find loans by status
     * @param status the loan status (e.g., ACTIVE, RETURNED, OVERDUE)
     * @return list of loans with the specified status
     */
    List<Loan> findByStatus(String status);
    
    /**
     * Find active loans by user ID
     * @param userId the user ID
     * @return list of active loans for the specified user
     */
    List<Loan> findActiveByUserId(Integer userId);
    
    /**
     * Find overdue loans (due date passed and status is ACTIVE)
     * @param currentDate the current date
     * @return list of overdue loans
     */
    List<Loan> findOverdueLoans(LocalDate currentDate);
    
    /**
     * Find loans due soon (within specified days)
     * @param currentDate the current date
     * @param daysAhead number of days ahead to check
     * @return list of loans due soon
     */
    List<Loan> findLoansDueSoon(LocalDate currentDate, int daysAhead);
    
    /**
     * Delete a loan by ID
     * @param loanId the loan ID
     * @return true if deleted successfully
     */
    boolean deleteById(Integer loanId);
    
    /**
     * Count active loans by user ID
     * @param userId the user ID
     * @return number of active loans
     */
    int countActiveByUserId(Integer userId);
    
    /**
     * Update loan status and return date
     * @param loanId the loan ID
     * @param status the new status
     * @param returnDate the return date (can be null)
     */
    void updateStatus(Integer loanId, String status, LocalDate returnDate);
}
