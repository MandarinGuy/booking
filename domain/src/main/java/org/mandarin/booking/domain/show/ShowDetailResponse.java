package org.mandarin.booking.domain.show;

import com.querydsl.core.annotations.QueryProjection;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;

public record ShowDetailResponse(Long showId, String title, Type type, Rating rating, String synopsis, String posterUrl,
                                 LocalDate performanceStartDate, LocalDate performanceEndDate, Long hallId,
                                 String hallName, List<ShowScheduleResponse> schedules) {
    @QueryProjection
    public ShowDetailResponse {
    }

    @Getter
    public static class ShowScheduleResponse {
        private final Long scheduleId;
        private final LocalDateTime startAt;
        private final LocalDateTime endAt;
        private final long runtimeMinutes;

        @QueryProjection
        public ShowScheduleResponse(Long scheduleId, LocalDateTime startAt, LocalDateTime endAt) {
            this.scheduleId = scheduleId;
            this.startAt = startAt;
            this.endAt = endAt;
            this.runtimeMinutes = Duration.between(startAt, endAt).toMinutes();
        }
    }
}
