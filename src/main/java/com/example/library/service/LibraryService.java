package com.example.library.service;

import com.example.library.domain.Loan;
import com.example.library.domain.MediaItem;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for core library operations.
 * Handles media item management, borrowing, returning, and loan tracking.
 */
public interface LibraryService {
    
    /**
     * Adds a new media item to the library collection.
     * 
     * @param item the media item to add
     * @return the saved media item with generated ID
     */
    MediaItem addMediaItem(MediaItem item);
    
    /**
     * Searches for media items by keyword.
     * Searches across title, author, ISBN, and publisher fields.
     * 
     * @param keyword the search keyword
     * @return list of matching media items
     */
    List<MediaItem> searchItems(String keyword);
    
    /**
     * Processes a borrowing request for a user.
     * Validates user eligibility, item availability, and creates a loan record.
     * 
     * @param userId the ID of the user borrowing the item
     * @param itemId the ID of the media item to borrow
     * @param today the current date (used for loan and due date calculation)
     * @return the created Loan object
     * @throws BusinessException if user is not eligible or item is unavailable
     */
    Loan borrowItem(int userId, int itemId, LocalDate today);
    
    /**
     * Processes the return of a borrowed item.
     * Updates loan status and increments available copies.
     * 
     * @param loanId the ID of the loan to return
     * @param returnDate the date the item is returned
     * @throws BusinessException if loan not found or already returned
     */
    void returnItem(int loanId, LocalDate returnDate);
    
    /**
     * Retrieves all overdue loans as of the specified date.
     * 
     * @param today the current date to check against due dates
     * @return list of overdue loans
     */
    List<Loan> getOverdueLoans(LocalDate today);
    
    /**
     * Retrieves all loans for a specific user.
     * 
     * @param userId the ID of the user
     * @return list of loans for the user
     */
    List<Loan> getUserLoans(int userId);
    
    /**
     * Checks if a user is eligible to borrow items.
     * User must have no overdue loans and no unpaid fines.
     * 
     * @param userId the ID of the user to check
     * @param today the current date
     * @return true if user can borrow, false otherwise
     */
    boolean canUserBorrow(int userId, LocalDate today);
}
