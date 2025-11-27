package com.example.library.service.fine;

import java.math.BigDecimal;

/**
 * Fine calculation strategy for CDs.
 * Charges 20 NIS per overdue day.
 */
public class CDFineStrategy implements FineStrategy {
    
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("20.00");
    
    /**
     * Calculates the fine for an overdue CD.
     * 
     * @param overdueDays the number of days the CD is overdue
     * @return the calculated fine amount (20 NIS × overdue days)
     */
    @Override
    public BigDecimal calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return FINE_PER_DAY.multiply(BigDecimal.valueOf(overdueDays));
    }
}
