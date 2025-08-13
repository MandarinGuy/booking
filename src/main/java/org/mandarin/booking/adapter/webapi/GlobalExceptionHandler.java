package org.mandarin.booking.adapter.webapi;

import org.mandarin.booking.domain.error.DomainException;
import org.mandarin.booking.domain.error.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleJsonParseError(DomainException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }
}
