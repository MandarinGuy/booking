package org.mandarin.booking.domain.show;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

public record ShowScheduleRegisterRequest(
        Long showId,
        Long hallId,
        LocalDateTime startAt,
        LocalDateTime endAt,

        @Min(value = 1, message = "상영 시간은 최소 1분 이상이어야 합니다.")
        Integer runtimeMinutes
) {
}
