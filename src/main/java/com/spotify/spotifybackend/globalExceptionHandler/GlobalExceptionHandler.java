package com.spotify.spotifybackend.globalExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Ovo znaci: "Slusaj greske u svim kontrolerima"
public class GlobalExceptionHandler {

    // 1. HVATA GRESKE VALIDACIJE (@Valid u DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Prolazimo kroz sve greske koje je Spring nasao
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // Npr. "username"
            String errorMessage = error.getDefaultMessage();    // Npr. "Username is required"
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 2. HVATA GRESKE IZ SERVISA (Ono sto se baca sa 'throw new IllegalArgumentException')
    // Npr. "Passwords do not match" ili "Username already exists"
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleLogicExceptions(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
