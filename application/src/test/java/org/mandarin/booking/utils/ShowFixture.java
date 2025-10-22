package org.mandarin.booking.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.ShowRegisterRequest.GradeRequest;
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

    public static ShowScheduleRegisterRequest generateShowScheduleRegisterRequest(Show show, Long sectionId,
                                                                                  Map<Long, List<Long>> gradeSeatMap) {
        return generateShowScheduleRegisterRequest(show, sectionId,
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30), gradeSeatMap);
    }

    public static ShowScheduleRegisterRequest generateShowScheduleRegisterRequest(Show show,
                                                                                  Long sectionId,
                                                                                  LocalDateTime startAt,
                                                                                  LocalDateTime endAt,
                                                                                  Map<Long, List<Long>> gradeSeatMap) {
        return new ShowScheduleRegisterRequest(
                show.getId(),
                startAt,
                endAt,
                getSeatUsageRequest(sectionId, gradeSeatMap)
        );
    }

    public static SeatUsageRequest getSeatUsageRequest(long sectionId, Map<Long, List<Long>> gradeSeatMap) {
        return new SeatUsageRequest(
                sectionId,
                List.of(),
                gradeSeatMap.entrySet().stream()
                        .map(entry -> new GradeAssignmentRequest(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }

    static List<GradeRequest> generateGradeRequest(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new GradeRequest(
                        UUID.randomUUID().toString().substring(0, 5),
                        random.nextInt(100) * 1000,
                        100
                ))
                .toList();
    }
}
