package com.example.library.service.fine;

import java.math.BigDecimal;

/**
 * Strategy interface for calculating fines based on overdue days.
 * Different implementations can provide different fine calculation logic
 * based on media type or other business rules.
 */
public interface FineStrategy {
    
    /**
     * Calculates the fine amount based on the number of overdue days.
     * 
     * @param overdueDays the number of days the item is overdue
     * @return the calculated fine amount
     */
    BigDecimal calculateFine(long overdueDays);
}
