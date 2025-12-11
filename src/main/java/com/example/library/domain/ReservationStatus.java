package com.example.library.domain;

/**
 * Constants for reservation status values.
 */
public final class ReservationStatus {
    public static final String ACTIVE = "ACTIVE";
    public static final String CANCELLED = "CANCELLED";
    public static final String FULFILLED = "FULFILLED";
    public static final String EXPIRED = "EXPIRED";
    
    private ReservationStatus() {
        // Private constructor to prevent instantiation
    }
}
