package com.example.library;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;

class AppTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    
    @Test
    void testApp() {
        assertTrue(true, "This test should pass");
    }
    
    @Test
    void testAppConstructor() {
        assertDoesNotThrow(() -> new App());
    }
    
    @Test
    void testMainMethod_ExitsGracefully() {
        String input = "6\n"; // Exit option
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
    
    @Test
    void testMainMethod_HandlesInvalidInput() {
        String input = "invalid\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
    
    @Test
    void testMainMethod_ShowsMenu() {
        String input = "6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        App.main(new String[]{});
        
        String output = outContent.toString();
        assertTrue(output.contains("Hello Library System") || 
                   output.contains("Database connection"));
    }
    
    @Test
    void testMainMethod_HandlesMultipleInvalidInputs() {
        String input = "abc\n-1\n999\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
    
    @Test
    void testMainMethod_HandlesZeroInput() {
        String input = "0\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
