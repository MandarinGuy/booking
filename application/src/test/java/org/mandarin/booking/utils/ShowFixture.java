package org.mandarin.booking.utils;

import java.time.LocalDateTime;
import java.util.Random;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.ShowScheduleCreateCommand;

public class ShowFixture {
    static ShowScheduleCreateCommand generateShowScheduleCreateCommand(Show show) {
        Random random = new Random();
        var startAt = LocalDateTime.now().plusDays(random.nextInt(0, 10));
        return new ShowScheduleCreateCommand(show.getId(),
                startAt,
                startAt.plusHours(random.nextInt(2, 5))
        );
    }
}
