package org.mandarin.booking.domain.error;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
