package org.mandarin.booking.domain.show;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    @AssertTrue(message = "The runtime must match the difference between start and end times")
    private boolean isRuntimeValid() {
        long between = ChronoUnit.MINUTES.between(startAt, endAt);
        return between == runtimeMinutes;
    }
}
