package com.spotify.spotifybackend.globalExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonErrors(HttpMessageNotReadableException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        // Proveravamo da li je greška nastala zbog polja 'age' ili nekog drugog Int/Long polja
        if (ex.getMessage().contains("age") || ex.getMessage().contains("int")) {
            errorResponse.put("error", "Field 'age' must be a number, not text.");
        } else {
            // Generalna poruka ako je nešto drugo loše formatirano u JSON-u
            errorResponse.put("error", "You have submitted an incorrect data format. " +
                        "Make sure you entered text where a number is expected.");
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Dodaj ovo u GlobalExceptionHandler.java ako vec nemas

    // Hvata greske validacije za @ModelAttribute (Multipart forme)
    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<java.util.Map<String, String>> handleBindException(org.springframework.validation.BindException ex) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
