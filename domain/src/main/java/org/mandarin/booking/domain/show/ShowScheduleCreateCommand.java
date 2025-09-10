package org.mandarin.booking.domain.show;

import java.time.Duration;
import java.time.LocalDateTime;

public record ShowScheduleCreateCommand(Long showId, LocalDateTime startAt, LocalDateTime endAt) {
    public int getRuntimeMinutes() {
        return (int) Duration.between(startAt, endAt).toMinutes();
    }
}
