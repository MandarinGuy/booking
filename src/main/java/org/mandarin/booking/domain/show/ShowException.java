package org.mandarin.booking.domain.show;

import org.mandarin.booking.adapter.webapi.ApiStatus;
import org.mandarin.booking.domain.DomainException;

public class ShowException extends DomainException {
    public ShowException(String message) {
        super(message);
    }

    public ShowException(ApiStatus status, String message) {
        super(status, message);
    }
}
