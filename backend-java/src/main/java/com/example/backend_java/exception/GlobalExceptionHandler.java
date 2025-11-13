package com.example.backend_java.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

// @ControllerAdvice lets this class watch all controllers for errors
@ControllerAdvice
public class GlobalExceptionHandler {

    // Requirement Met: Exception Handling
    // This method will run if any controller throws an IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        // Return a 400 Bad Request error with a clean JSON message
        return new ResponseEntity<>(
            Map.of("error", e.getMessage()), // e.g., "error": "Order must contain at least one item."
            HttpStatus.BAD_REQUEST
        );
    }

    // This is a safety net for any other server errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        e.printStackTrace(); // Log the full error to the console for you to see
        
        // Return a 500 Internal Server Error
        return new ResponseEntity<>(
            Map.of("error", "An unexpected server error occurred."),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}