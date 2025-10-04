package org.mandarin.booking.domain.show;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;

public record ShowDetailResponse(Long showId, String title, Type type, Rating rating, String synopsis, String posterUrl,
                                 LocalDate performanceStartDate, LocalDate performanceEndDate, Long hallId,
                                 String hallName, List<ShowScheduleResponse> schedules,
                                 List<GradeResponse> grades) {

    public record ShowScheduleResponse(Long scheduleId, LocalDateTime startAt, LocalDateTime endAt,
                                       long runtimeMinutes) {
    }
}
