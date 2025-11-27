package com.example.library.notification;

import com.example.library.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Email notification implementation of the Notifier interface.
 * Currently stores messages in memory for testing purposes.
 * In production, this would integrate with an actual email service.
 */
public class EmailNotifier implements Notifier {
    
    private final List<String> sentMessages;
    
    /**
     * Constructs a new email notifier.
     */
    public EmailNotifier() {
        this.sentMessages = new ArrayList<>();
    }
    
    /**
     * Sends an email notification to a user.
     * Currently simulates email sending by storing the message in memory.
     * 
     * @param user the user to notify
     * @param message the notification message
     */
    @Override
    public void notify(User user, String message) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
        
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        
        String emailMessage = user.getEmail() + ": " + message;
        sentMessages.add(emailMessage);
        
        // In production, this would send an actual email:
        // emailService.send(user.getEmail(), "Library Notification", message);
    }
    
    /**
     * Retrieves all sent messages.
     * Returns an unmodifiable list for testing purposes.
     * 
     * @return list of all sent email messages
     */
    public List<String> getSentMessages() {
        return Collections.unmodifiableList(sentMessages);
    }
    
    /**
     * Clears all sent messages.
     * Useful for resetting state between tests.
     */
    public void clearMessages() {
        sentMessages.clear();
    }
}
