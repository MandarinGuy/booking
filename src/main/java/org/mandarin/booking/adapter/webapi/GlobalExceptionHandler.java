package org.mandarin.booking.adapter.webapi;

import static java.util.Objects.requireNonNull;
import static org.mandarin.booking.adapter.webapi.ApiStatus.*;

import org.mandarin.booking.domain.DomainException;
import org.mandarin.booking.domain.member.AuthException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ErrorResponse handleJsonParseError(DomainException ex) {
        return new ErrorResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    public ErrorResponse handleAuthException(AuthException ex) {
        return new ErrorResponse(UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        return new ErrorResponse(BAD_REQUEST,
                requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
    }
}
