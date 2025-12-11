package com.example.library.domain;

/**
 * Constants for loan status values.
 */
public final class LoanStatus {
    public static final String ACTIVE = "ACTIVE";
    public static final String RETURNED = "RETURNED";
    public static final String OVERDUE = "OVERDUE";
    
    private LoanStatus() {
        // Private constructor to prevent instantiation
    }
}
