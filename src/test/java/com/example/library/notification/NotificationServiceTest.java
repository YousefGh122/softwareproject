package com.example.library.notification;

import com.example.library.model.User;
import com.example.library.model.Loan;
import com.example.library.model.MediaItem;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class NotificationServiceTest {
    
    private NotificationService notificationService;
    private User testUser;
    private Loan testLoan;
    private MediaItem testItem;
    
    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
        testUser = new User(1L, "testuser", "test@example.com", "password", "MEMBER");
        
        testItem = new MediaItem();
        testItem.setId(1L);
        testItem.setTitle("Test Book");
        testItem.setAuthor("Test Author");
        
        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setUserId(1L);
        testLoan.setMediaItemId(1L);
        testLoan.setLoanDate(LocalDate.now().minusDays(14));
        testLoan.setDueDate(LocalDate.now().plusDays(1));
        testLoan.setReturnDate(null);
    }
    
    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new NotificationService());
    }
    
    @Test
    void testSendOverdueNotification() {
        assertDoesNotThrow(() -> 
            notificationService.sendOverdueNotification(testUser, testLoan)
        );
    }
    
    @Test
    void testSendDueDateReminder() {
        assertDoesNotThrow(() -> 
            notificationService.sendDueDateReminder(testUser, testLoan)
        );
    }
    
    @Test
    void testSendReservationNotification() {
        assertDoesNotThrow(() -> 
            notificationService.sendReservationNotification(testUser, "Test Book")
        );
    }
    
    @Test
    void testSendReservationNotification_EmptyTitle() {
        assertDoesNotThrow(() -> 
            notificationService.sendReservationNotification(testUser, "")
        );
    }
    
    @Test
    void testSendNotification_WithNullUser() {
        assertThrows(NullPointerException.class, () -> 
            notificationService.sendOverdueNotification(null, testLoan)
        );
    }
    
    @Test
    void testSendNotification_WithNullLoan() {
        assertThrows(NullPointerException.class, () -> 
            notificationService.sendOverdueNotification(testUser, null)
        );
    }
    
    @Test
    void testSendNotification_WithNullEmail() {
        User userWithoutEmail = new User(2L, "nomail", null, "password", "MEMBER");
        assertThrows(NullPointerException.class, () -> 
            notificationService.sendOverdueNotification(userWithoutEmail, testLoan)
        );
    }
    
    @Test
    void testSendNotification_OverdueLoan() {
        testLoan.setDueDate(LocalDate.now().minusDays(5));
        assertDoesNotThrow(() -> 
            notificationService.sendOverdueNotification(testUser, testLoan)
        );
    }
    
    @Test
    void testSendNotification_LongOverdue() {
        testLoan.setDueDate(LocalDate.now().minusDays(30));
        assertDoesNotThrow(() -> 
            notificationService.sendOverdueNotification(testUser, testLoan)
        );
    }
}
