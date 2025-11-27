package com.example.library.service;

/**
 * Exception thrown when a business rule is violated.
 * This is an unchecked exception to simplify error handling in the application.
 */
public class BusinessException extends RuntimeException {
    
    /**
     * Constructs a new business exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new business exception with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
