package org.mandarin.booking.domain.show;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

public record ShowScheduleRegisterRequest(
        Long showId,
        Long hallId,
        LocalDateTime startAt,
        LocalDateTime endAt,

        @Min(value = 1, message = "The screening time should be at least 1 minute")
        Integer runtimeMinutes
) {
    @AssertTrue(message = "The end time must be after the start time")
    private boolean isEndAfterStart() {
        return endAt.isAfter(startAt);
    }
}
