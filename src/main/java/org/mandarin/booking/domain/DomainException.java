package org.mandarin.booking.domain;

import lombok.Getter;
import org.mandarin.booking.adapter.webapi.ApiStatus;

@Getter
public class DomainException extends RuntimeException {
    private ApiStatus status = ApiStatus.INTERNAL_SERVER_ERROR;
    public DomainException(String message) {
        super(message);
    }

    public DomainException(ApiStatus status, String message) {
        super(message);
        this.status = status;
    }
}
