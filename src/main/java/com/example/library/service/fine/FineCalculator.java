package com.example.library.service.fine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculator for library fines using the Strategy pattern.
 * Selects the appropriate fine calculation strategy based on media type.
 */
public class FineCalculator {
    
    private final Map<String, FineStrategy> strategies;
    
    /**
     * Constructs a new fine calculator with default strategies.
     * Initializes strategies for BOOK and CD media types.
     */
    public FineCalculator() {
        this.strategies = new HashMap<>();
        this.strategies.put("BOOK", new BookFineStrategy());
        this.strategies.put("CD", new CDFineStrategy());
    }
    
    /**
     * Calculates the fine for a media item based on its type and overdue days.
     * 
     * @param mediaType the type of media (e.g., "BOOK", "CD")
     * @param overdueDays the number of days the item is overdue
     * @return the calculated fine amount
     * @throws IllegalArgumentException if the media type is not supported
     */
    public BigDecimal calculateFine(String mediaType, long overdueDays) {
        if (mediaType == null || mediaType.trim().isEmpty()) {
            throw new IllegalArgumentException("Media type cannot be null or empty");
        }
        
        String normalizedType = mediaType.trim().toUpperCase();
        
        FineStrategy strategy = strategies.get(normalizedType);
        
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        }
        
        return strategy.calculateFine(overdueDays);
    }
    
    /**
     * Registers a custom fine strategy for a specific media type.
     * This allows extending the calculator with additional media types.
     * 
     * @param mediaType the media type to register
     * @param strategy the fine calculation strategy
     */
    public void registerStrategy(String mediaType, FineStrategy strategy) {
        if (mediaType == null || mediaType.trim().isEmpty()) {
            throw new IllegalArgumentException("Media type cannot be null or empty");
        }
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        
        this.strategies.put(mediaType.trim().toUpperCase(), strategy);
    }
}
