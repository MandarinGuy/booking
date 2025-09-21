package org.mandarin.booking.domain.show;

import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

public record ShowScheduleRegisterRequest(
        Long showId,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    @AssertTrue(message = "The end time must be after the start time")
    private boolean isEndAfterStart() {
        return endAt.isAfter(startAt);
    }
}
