package org.mandarin.booking.domain;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private String status = "INTERNAL_SERVER_ERROR";
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String status, String message) {
        super(message);
        this.status = status;
    }
}
