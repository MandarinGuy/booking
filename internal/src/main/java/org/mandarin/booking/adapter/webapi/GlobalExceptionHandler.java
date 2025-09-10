package org.mandarin.booking.adapter.webapi;

import static java.util.Objects.requireNonNull;
import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.webapi.ApiStatus.NOT_FOUND;
import static org.mandarin.booking.adapter.webapi.ApiStatus.UNAUTHORIZED;

import lombok.extern.slf4j.Slf4j;
import org.mandarin.booking.AuthException;
import org.mandarin.booking.DomainException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ErrorResponse handleJsonParseError(DomainException ex) {
        log.error("Domain Exception: {}", (Object[]) ex.getStackTrace());
        var status = ex.getStatus();
        return new ErrorResponse(ApiStatus.valueOf(status), ex.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    public ErrorResponse handleAuthException(AuthException ex) {
        return new ErrorResponse(UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        var defaultMessage = requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return new ErrorResponse(BAD_REQUEST, requireNonNull(defaultMessage));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new ErrorResponse(NOT_FOUND, ex.getMessage());
    }
}
