package org.mandarin.booking.adapter.webapi;

import static java.util.Objects.requireNonNull;
import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.webapi.ApiStatus.NOT_FOUND;
import static org.mandarin.booking.adapter.webapi.ApiStatus.UNAUTHORIZED;

import org.mandarin.booking.domain.DomainException;
import org.mandarin.booking.domain.member.AuthException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ErrorResponse handleJsonParseError(DomainException ex) {
        return new ErrorResponse(ex.getStatus(), ex.getMessage());
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

    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new ErrorResponse(NOT_FOUND, ex.getMessage());
    }
}
