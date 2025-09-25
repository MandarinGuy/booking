package org.mandarin.booking.domain.show;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

public record ShowDetailResponse(
        Long showId,
        String title,
        String type,
        String rating,
        String synopsis,
        String posterUrl,
        LocalDate performanceStartDate,
        LocalDate performanceEndDate,
        HallResponse hall,
        List<ShowScheduleResponse> schedules
) {
    public record HallResponse(
            Long hallId,
            String hallName
    ) {
    }

    @Getter
    public static final class ShowScheduleResponse {
        private final Long scheduleId;
        private final LocalDateTime startAt;
        private final LocalDateTime endAt;
        private final long runtimeMinutes;

        public ShowScheduleResponse(Long scheduleId, LocalDateTime startAt, LocalDateTime endAt) {
            this.scheduleId = scheduleId;
            this.startAt = startAt;
            this.endAt = endAt;
            this.runtimeMinutes = Duration.between(startAt, endAt).toMinutes();
        }
    }
}
