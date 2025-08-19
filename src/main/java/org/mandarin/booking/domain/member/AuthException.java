package org.mandarin.booking.domain.member;

import org.mandarin.booking.domain.DomainException;

public class AuthException extends DomainException {
    public AuthException(String message) {
        super(message);
    }
}
