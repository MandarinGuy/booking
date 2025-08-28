package org.mandarin.booking.domain.movie;

import org.mandarin.booking.domain.DomainException;

public class MovieException extends DomainException {
    public MovieException(String message) {
        super(message);
    }
}
