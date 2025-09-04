package org.mandarin.booking.domain.show;

import java.time.LocalDate;
import lombok.Getter;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;

@Getter
public class ShowCreateCommand {
    private final String title;
    private final Type type;
    private final Rating rating;
    private final String synopsis;
    private final String posterUrl;
    private final LocalDate performanceStartDate;
    private final LocalDate performanceEndDate;

    private ShowCreateCommand(String title, Type type, Rating rating, String synopsis, String posterUrl,
                              LocalDate performanceStartDate, LocalDate performanceEndDate) {
        this.title = title;
        this.type = type;
        this.rating = rating;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.performanceStartDate = performanceStartDate;
        this.performanceEndDate = performanceEndDate;
    }

    public static ShowCreateCommand from(ShowRegisterRequest request) {
        return new ShowCreateCommand(
                request.title(),
                Type.valueOf(request.type()),
                Rating.valueOf(request.rating()),
                request.synopsis(),
                request.posterUrl(),
                request.performanceStartDate(),
                request.performanceEndDate()
        );
    }
}

