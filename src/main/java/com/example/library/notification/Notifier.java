package com.example.library.notification;

import com.example.library.domain.User;

/**
 * Observer-like interface for notification mechanisms.
 * Implementations can send notifications through various channels (email, SMS, etc.).
 */
public interface Notifier {
    
    /**
     * Sends a notification to a user.
     * 
     * @param user the user to notify
     * @param message the notification message
     */
    void notify(User user, String message);
}
