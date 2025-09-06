package org.mandarin.booking.app;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class HallVerificationEvent {
    private final Long hallId;
    private boolean verified = false;

    public void verify() {
        this.verified = true;
    }
}
