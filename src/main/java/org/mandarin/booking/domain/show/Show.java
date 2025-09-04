package org.mandarin.booking.domain.show;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Show extends AbstractEntity {
    private String title;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    private String synopsis;

    private String posterUrl;

    private LocalDate performanceStartDate;

    private LocalDate performanceEndDate;

    public static Show create(ShowCreateCommand command) {
        return new Show(
                command.getTitle(),
                command.getType(),
                command.getRating(),
                command.getSynopsis(),
                command.getPosterUrl(),
                command.getPerformanceStartDate(),
                command.getPerformanceEndDate()
        );
    }

    public enum Type {
        MUSICAL, PLAY, CONCERT, OPERA, DANCE, CLASSICAL, ETC
    }

    public enum Rating {
        ALL, AGE12, AGE15, AGE18
    }
}

