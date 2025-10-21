package org.mandarin.booking.utils;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.mandarin.booking.domain.hall.SeatRegisterRequest;
import org.mandarin.booking.domain.hall.SectionRegisterRequest;

public class HallFixture {
    public static String generateHallName() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    static List<SectionRegisterRequest> generateSectionRegisterRequest(int count) {
        return List.of(
                new SectionRegisterRequest(
                        UUID.randomUUID().toString().substring(0, 10),
                        IntStream.range(0, count)
                                .mapToObj(i -> new SeatRegisterRequest(
                                        UUID.randomUUID().toString().substring(0, 8),
                                        UUID.randomUUID().toString().substring(0, 8)
                                ))
                                .toList()
                )
        );
    }
}
