package org.mandarin.booking.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.ShowScheduleCreateCommand;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest.GradeAssignmentRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest.SeatUsageRequest;

public class ShowFixture {
    private static final Random random = new Random();
    static ShowScheduleCreateCommand generateShowScheduleCreateCommand(Show show) {
        var startAt = LocalDateTime.now().plusDays(random.nextInt(0, 10));
        return new ShowScheduleCreateCommand(show.getId(),
                startAt,
                startAt.plusHours(random.nextInt(2, 5))
        );
    }

    public static ShowScheduleRegisterRequest generateShowScheduleRegisterRequest(Show show, Long sectionId) {
        return generateShowScheduleRegisterRequest(show, sectionId,
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30));
    }

    public static ShowScheduleRegisterRequest generateShowScheduleRegisterRequest(Show show,
                                                                                  Long sectionId,
                                                                                  LocalDateTime startAt,
                                                                                  LocalDateTime endAt) {
        return new ShowScheduleRegisterRequest(
                show.getId(),
                startAt,
                endAt,
                getSeatUsageRequest(sectionId)
        );
    }

    public static SeatUsageRequest getSeatUsageRequest(long sectionId) {
        return new SeatUsageRequest(
                sectionId,
                List.of(),
                List.of(
                        new GradeAssignmentRequest(1L, List.of(1L, 2L, 3L)),
                        new GradeAssignmentRequest(2L, List.of(4L, 5L, 6L))
                )
        );
    }
}
