package org.mandarin.booking.domain.member;

import org.mandarin.booking.DomainException;

public class MemberException extends DomainException {
    public MemberException(String message) {
        super(message);
    }
}
