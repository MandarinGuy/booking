package org.mandarin.booking.app;

import java.time.LocalDateTime;

public record UnscheduledHallCheckEvent(
        Long hallId,
        LocalDateTime startAt,
        LocalDateTime endAt) {
}
