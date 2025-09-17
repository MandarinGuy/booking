package org.mandarin.booking.domain.show;

import java.time.LocalDate;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;

public record ShowResponse(
        Long showId,
        String title,
        Type type,
        Rating rating,
        String posterUrl,
        String venueName,
        LocalDate performanceStartDate,
        LocalDate performanceEndDate
) {
}
