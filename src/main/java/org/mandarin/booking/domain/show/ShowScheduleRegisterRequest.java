package org.mandarin.booking.domain.show;

import java.time.LocalDateTime;

public record ShowScheduleRegisterRequest(
        Long showId,
        Long hallId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Integer runtimeMinutes
) {
}
