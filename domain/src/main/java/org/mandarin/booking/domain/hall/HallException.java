package org.mandarin.booking.domain.hall;

import org.mandarin.booking.DomainException;

public class HallException extends DomainException {
    public HallException(String status, String message) {
        super(status, message);
    }
}
