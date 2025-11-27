package com.example.library.service;

import com.example.library.domain.Loan;
import com.example.library.domain.User;
import com.example.library.notification.Notifier;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for sending reminder notifications to users.
 * Uses the Observer pattern through the Notifier interface.
 */
public class ReminderService {
    
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final Notifier notifier;
    
    /**
     * Constructs a new reminder service.
     * 
     * @param loanRepository the repository for loan data
     * @param userRepository the repository for user data
     * @param notifier the notification mechanism to use
     */
    public ReminderService(LoanRepository loanRepository,
                          UserRepository userRepository,
                          Notifier notifier) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.notifier = notifier;
    }
    
    /**
     * Sends overdue reminders to all users with overdue loans.
     * Groups overdue loans by user and sends a single notification
     * to each user with the count of their overdue items.
     * 
     * @param today the current date to check against loan due dates
     */
    public void sendOverdueReminders(LocalDate today) {
        // Load all overdue loans
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(today);
        
        if (overdueLoans.isEmpty()) {
            return; // No overdue loans, nothing to do
        }
        
        // Group loans by userId and count them
        Map<Integer, Long> overdueLoansByUser = overdueLoans.stream()
                .collect(Collectors.groupingBy(
                        Loan::getUserId,
                        Collectors.counting()
                ));
        
        // Send notification to each user with overdue loans
        overdueLoansByUser.forEach((userId, count) -> {
            Optional<User> userOptional = userRepository.findById(userId);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String message = buildOverdueMessage(count);
                notifier.notify(user, message);
            }
        });
    }
    
    /**
     * Builds the overdue reminder message.
     * 
     * @param count the number of overdue items
     * @return formatted reminder message
     */
    private String buildOverdueMessage(long count) {
        if (count == 1) {
            return "You have 1 overdue book(s).";
        } else {
            return "You have " + count + " overdue book(s).";
        }
    }
}
