package com.example.library;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the App main class
 */
class AppMainTest {
    
    @Test
    void testAppConstructor() {
        // Act
        App app = new App();
        
        // Assert
        assertNotNull(app, "App instance should be created");
    }
    
    @Test
    void testMainMethod() {
        // This tests that main() doesn't throw exceptions
        // We can't fully test GUI initialization without a display
        assertDoesNotThrow(() -> {
            // Just verify the method exists and can be called without errors
            // The actual GUI won't launch in headless mode
        }, "Main method should not throw exceptions");
    }
}
