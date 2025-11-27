package com.example.library.service;

import com.example.library.domain.Fine;
import com.example.library.domain.Loan;
import com.example.library.domain.MediaItem;
import com.example.library.domain.User;
import com.example.library.repository.FineRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.MediaItemRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.fine.FineCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the library service.
 * Provides core library operations including borrowing, returning, and searching.
 */
public class LibraryServiceImpl implements LibraryService {
    
    private final UserRepository userRepository;
    private final MediaItemRepository mediaItemRepository;
    private final LoanRepository loanRepository;
    private final FineRepository fineRepository;
    private final FineCalculator fineCalculator;
    
    /**
     * Constructs a new library service with the specified repositories and fine calculator.
     * 
     * @param userRepository the repository for user data
     * @param mediaItemRepository the repository for media item data
     * @param loanRepository the repository for loan data
     * @param fineRepository the repository for fine data
     * @param fineCalculator the calculator for fine amounts using Strategy pattern
     */
    public LibraryServiceImpl(UserRepository userRepository,
                              MediaItemRepository mediaItemRepository,
                              LoanRepository loanRepository,
                              FineRepository fineRepository,
                              FineCalculator fineCalculator) {
        this.userRepository = userRepository;
        this.mediaItemRepository = mediaItemRepository;
        this.loanRepository = loanRepository;
        this.fineRepository = fineRepository;
        this.fineCalculator = fineCalculator;
    }
    
    /**
     * Adds a new media item to the library collection.
     * 
     * @param item the media item to add
     * @return the saved media item with generated ID
     */
    @Override
    public MediaItem addMediaItem(MediaItem item) {
        if (item == null) {
            throw new BusinessException("Media item cannot be null");
        }
        return mediaItemRepository.save(item);
    }
    
    /**
     * Searches for media items by keyword.
     * Delegates to the repository search method.
     * 
     * @param keyword the search keyword
     * @return list of matching media items
     */
    @Override
    public List<MediaItem> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return mediaItemRepository.findAll();
        }
        return mediaItemRepository.search(keyword);
    }
    
    /**
     * Processes a borrowing request for a user.
     * Validates eligibility, creates loan, and updates available copies.
     * 
     * @param userId the ID of the user borrowing the item
     * @param itemId the ID of the media item to borrow
     * @param today the current date (used for loan and due date calculation)
     * @return the created Loan object
     * @throws BusinessException if user is not eligible or item is unavailable
     */
    @Override
    public Loan borrowItem(int userId, int itemId, LocalDate today) {
        // Validate user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new BusinessException("User not found with ID: " + userId);
        }
        
        // Validate media item exists and has available copies
        Optional<MediaItem> itemOptional = mediaItemRepository.findById(itemId);
        if (!itemOptional.isPresent()) {
            throw new BusinessException("Media item not found with ID: " + itemId);
        }
        
        MediaItem item = itemOptional.get();
        if (item.getAvailableCopies() <= 0) {
            throw new BusinessException("No available copies of: " + item.getTitle());
        }
        
        // Check user eligibility
        if (!canUserBorrow(userId, today)) {
            throw new BusinessException("User is not eligible to borrow. " +
                    "Please return overdue items or pay outstanding fines.");
        }
        
        // Determine loan period based on media type
        LocalDate dueDate;
        String mediaType = item.getType();
        if ("BOOK".equalsIgnoreCase(mediaType)) {
            dueDate = today.plusDays(28);
        } else if ("CD".equalsIgnoreCase(mediaType)) {
            dueDate = today.plusDays(7);
        } else {
            // Default loan period for other media types
            dueDate = today.plusDays(14);
        }
        
        // Create and save loan
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setItemId(itemId);
        loan.setLoanDate(today);
        loan.setDueDate(dueDate);
        loan.setReturnDate(null);
        loan.setStatus("ACTIVE");
        
        Loan savedLoan = loanRepository.save(loan);
        
        // Decrement available copies
        int newAvailableCopies = item.getAvailableCopies() - 1;
        mediaItemRepository.updateAvailableCopies(itemId, newAvailableCopies);
        
        return savedLoan;
    }
    
    /**
     * Processes the return of a borrowed item.
     * Updates loan status, increments available copies, and calculates fines if overdue.
     * Uses Strategy pattern for fine calculation based on media type.
     * 
     * @param loanId the ID of the loan to return
     * @param returnDate the date the item is returned
     * @throws BusinessException if loan not found or already returned
     */
    @Override
    public void returnItem(int loanId, LocalDate returnDate) {
        // 1) Load the loan by id
        Optional<Loan> loanOptional = loanRepository.findById(loanId);
        if (!loanOptional.isPresent()) {
            throw new BusinessException("Loan not found with ID: " + loanId);
        }
        
        Loan loan = loanOptional.get();
        
        // 2) Check if already returned
        if (loan.getReturnDate() != null || "RETURNED".equalsIgnoreCase(loan.getStatus())) {
            throw new BusinessException("Loan has already been returned");
        }
        
        // 3) Set returnDate and status "RETURNED" and update the loan
        loanRepository.updateStatus(loanId, "RETURNED", returnDate);
        
        // 4) Load the related MediaItem by itemId
        Optional<MediaItem> itemOptional = mediaItemRepository.findById(loan.getItemId());
        if (!itemOptional.isPresent()) {
            throw new BusinessException("Media item not found with ID: " + loan.getItemId());
        }
        
        MediaItem item = itemOptional.get();
        
        // 5) Increment availableCopies and update the media item
        int newAvailableCopies = item.getAvailableCopies() + 1;
        mediaItemRepository.updateAvailableCopies(loan.getItemId(), newAvailableCopies);
        
        // 6) Check if the item is overdue and calculate fine using Strategy pattern
        // STRATEGY PATTERN IMPLEMENTATION: Fine calculation based on media type
        // - BookFineStrategy: 10 NIS per day
        // - CDFineStrategy: 20 NIS per day
        if (returnDate.isAfter(loan.getDueDate())) {
            // Calculate overdue days
            long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
            
            // Use FineCalculator (Strategy pattern) to calculate fine based on media type
            BigDecimal fineAmount = fineCalculator.calculateFine(item.getType(), overdueDays);
            
            // If amount > 0, create and save a fine
            if (fineAmount.compareTo(BigDecimal.ZERO) > 0) {
                Fine fine = new Fine();
                fine.setLoanId(loanId);
                fine.setAmount(fineAmount);
                fine.setIssuedDate(returnDate);
                fine.setStatus("UNPAID");
                fine.setPaidDate(null);
                
                fineRepository.save(fine);
            }
        }
    }
    
    /**
     * Retrieves all overdue loans as of the specified date.
     * Delegates to the repository.
     * 
     * @param today the current date to check against due dates
     * @return list of overdue loans
     */
    @Override
    public List<Loan> getOverdueLoans(LocalDate today) {
        return loanRepository.findOverdueLoans(today);
    }
    
    /**
     * Retrieves all loans for a specific user.
     * Delegates to the repository.
     * 
     * @param userId the ID of the user
     * @return list of loans for the user
     */
    @Override
    public List<Loan> getUserLoans(int userId) {
        return loanRepository.findByUserId(userId);
    }
    
    /**
     * Checks if a user is eligible to borrow items.
     * User must have no overdue loans and no unpaid fines.
     * 
     * @param userId the ID of the user to check
     * @param today the current date
     * @return true if user can borrow, false otherwise
     */
    @Override
    public boolean canUserBorrow(int userId, LocalDate today) {
        // Check for overdue loans
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(today);
        boolean hasOverdueLoans = overdueLoans.stream()
                .anyMatch(loan -> loan.getUserId().equals(userId));
        
        if (hasOverdueLoans) {
            return false;
        }
        
        // Check for unpaid fines
        BigDecimal totalUnpaidFines = fineRepository.calculateTotalUnpaidByUserId(userId);
        if (totalUnpaidFines.compareTo(BigDecimal.ZERO) > 0) {
            return false;
        }
        
        return true;
    }
}
