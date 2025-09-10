package org.mandarin.booking.domain.show;

import org.mandarin.booking.DomainException;

public class ShowException extends DomainException {
    public ShowException(String message) {
        super(message);
    }

    public ShowException(String status, String message) {
        super(status, message);
    }
}
