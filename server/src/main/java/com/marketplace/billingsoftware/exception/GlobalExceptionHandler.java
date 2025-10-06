package com.marketplace.billingsoftware.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (ex.getCause() instanceof ConstraintViolationException ||
                ex.getMostSpecificCause().getMessage().contains("Duplicate entry")) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A category with that name already exists.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Database error: " + ex.getMessage());
    }

}
