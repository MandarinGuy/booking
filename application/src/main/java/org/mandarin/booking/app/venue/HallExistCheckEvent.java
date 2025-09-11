package org.mandarin.booking.app.venue;

import lombok.Getter;

@Getter
public class HallExistCheckEvent {
    private final Long hallId;
    private boolean exist = false;

    public HallExistCheckEvent(Long hallId) {
        this.hallId = hallId;
    }

    public void exist() {
        this.exist = true;
    }
}
