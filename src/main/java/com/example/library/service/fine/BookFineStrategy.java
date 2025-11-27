package com.example.library.service.fine;

import java.math.BigDecimal;

/**
 * Fine calculation strategy for books.
 * Charges 10 NIS per overdue day.
 */
public class BookFineStrategy implements FineStrategy {
    
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("10.00");
    
    /**
     * Calculates the fine for an overdue book.
     * 
     * @param overdueDays the number of days the book is overdue
     * @return the calculated fine amount (10 NIS × overdue days)
     */
    @Override
    public BigDecimal calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return FINE_PER_DAY.multiply(BigDecimal.valueOf(overdueDays));
    }
}
