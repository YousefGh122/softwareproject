package com.example.library.service;

import com.example.library.domain.Loan;
import com.example.library.domain.User;
import com.example.library.notification.Notifier;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ReminderServiceTest {
    
    private LoanRepository loanRepository;
    private UserRepository userRepository;
    private Notifier notifier;
    private ReminderService reminderService;
    
    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        userRepository = mock(UserRepository.class);
        notifier = mock(Notifier.class);
        
        reminderService = new ReminderService(
                loanRepository,
                userRepository,
                notifier
        );
    }
    
    @Test
    void testSendOverdueReminders_multipleUsersWithOverdueLoans() {
        // Arrange
        LocalDate today = LocalDate.of(2025, 11, 27);
        
        // Create 2 overdue loans for user 1
        Loan loan1User1 = new Loan();
        loan1User1.setLoanId(1);
        loan1User1.setUserId(1);
        loan1User1.setItemId(10);
        loan1User1.setLoanDate(LocalDate.of(2025, 10, 1));
        loan1User1.setDueDate(LocalDate.of(2025, 11, 1));
        loan1User1.setReturnDate(null);
        loan1User1.setStatus("ACTIVE");
        
        Loan loan2User1 = new Loan();
        loan2User1.setLoanId(2);
        loan2User1.setUserId(1);
        loan2User1.setItemId(11);
        loan2User1.setLoanDate(LocalDate.of(2025, 10, 5));
        loan2User1.setDueDate(LocalDate.of(2025, 11, 5));
        loan2User1.setReturnDate(null);
        loan2User1.setStatus("ACTIVE");
        
        // Create 1 overdue loan for user 2
        Loan loan1User2 = new Loan();
        loan1User2.setLoanId(3);
        loan1User2.setUserId(2);
        loan1User2.setItemId(20);
        loan1User2.setLoanDate(LocalDate.of(2025, 10, 10));
        loan1User2.setDueDate(LocalDate.of(2025, 11, 10));
        loan1User2.setReturnDate(null);
        loan1User2.setStatus("ACTIVE");
        
        List<Loan> overdueLoans = Arrays.asList(loan1User1, loan2User1, loan1User2);
        
        // Create users with emails
        User user1 = new User();
        user1.setUserId(1);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRole("STUDENT");
        
        User user2 = new User();
        user2.setUserId(2);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRole("STUDENT");
        
        // Mock repository responses
        when(loanRepository.findOverdueLoans(today)).thenReturn(overdueLoans);
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2)).thenReturn(Optional.of(user2));
        
        // Act
        reminderService.sendOverdueReminders(today);
        
        // Assert
        // Verify notifier.notify was called exactly 2 times (once per user)
        verify(notifier, times(2)).notify(any(User.class), anyString());
        
        // Verify user 1 received notification with 2 overdue books
        verify(notifier).notify(user1, "You have 2 overdue book(s).");
        
        // Verify user 2 received notification with 1 overdue book
        verify(notifier).notify(user2, "You have 1 overdue book(s).");
        
        // Verify repositories were called
        verify(loanRepository).findOverdueLoans(today);
        verify(userRepository).findById(1);
        verify(userRepository).findById(2);
    }
    
    @Test
    void testSendOverdueReminders_noOverdueLoans() {
        // Arrange
        LocalDate today = LocalDate.of(2025, 11, 27);
        
        when(loanRepository.findOverdueLoans(today)).thenReturn(Arrays.asList());
        
        // Act
        reminderService.sendOverdueReminders(today);
        
        // Assert
        verify(loanRepository).findOverdueLoans(today);
        verify(notifier, never()).notify(any(User.class), anyString());
        verify(userRepository, never()).findById(anyInt());
    }
    
    @Test
    void testSendOverdueReminders_userNotFound() {
        // Arrange
        LocalDate today = LocalDate.of(2025, 11, 27);
        
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setUserId(999);
        loan.setItemId(10);
        loan.setLoanDate(LocalDate.of(2025, 10, 1));
        loan.setDueDate(LocalDate.of(2025, 11, 1));
        loan.setReturnDate(null);
        loan.setStatus("ACTIVE");
        
        when(loanRepository.findOverdueLoans(today)).thenReturn(Arrays.asList(loan));
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act
        reminderService.sendOverdueReminders(today);
        
        // Assert
        verify(loanRepository).findOverdueLoans(today);
        verify(userRepository).findById(999);
        verify(notifier, never()).notify(any(User.class), anyString());
    }
    
    @Test
    void testSendOverdueReminders_singleUserWithSingleOverdueBook() {
        // Arrange
        LocalDate today = LocalDate.of(2025, 11, 27);
        
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setUserId(5);
        loan.setItemId(50);
        loan.setLoanDate(LocalDate.of(2025, 10, 1));
        loan.setDueDate(LocalDate.of(2025, 11, 1));
        loan.setReturnDate(null);
        loan.setStatus("ACTIVE");
        
        User user = new User();
        user.setUserId(5);
        user.setUsername("user5");
        user.setEmail("user5@example.com");
        user.setRole("FACULTY");
        
        when(loanRepository.findOverdueLoans(today)).thenReturn(Arrays.asList(loan));
        when(userRepository.findById(5)).thenReturn(Optional.of(user));
        
        // Act
        reminderService.sendOverdueReminders(today);
        
        // Assert
        verify(notifier).notify(user, "You have 1 overdue book(s).");
        verify(notifier, times(1)).notify(any(User.class), anyString());
    }
}
