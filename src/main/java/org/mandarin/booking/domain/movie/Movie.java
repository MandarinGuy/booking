package org.mandarin.booking.domain.movie;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Movie extends AbstractEntity {
    private String title;

    private String director;

    private Integer runtimeMinutes;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    private String synopsis;

    private String posterUrl;


    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor_name")
    private Set<String> casts = new HashSet<>();

    public static Movie create(MovieCreateCommand command) {
        return new Movie(command.getTitle(), command.getDirector(), command.getRuntimeMinutes(), command.getGenre(),
                command.getReleaseDate(), command.getRating(), command.getSynopsis(), command.getPosterUrl(), command.getCasts());
    }

    public enum Genre {
        ACTION, DRAMA, COMEDY, THRILLER, ROMANCE, SF, FANTASY, HORROR, ANIMATION, DOCUMENTARY, ETC
    }

    public enum Rating {
        ALL, AGE12, AGE15, AGE18
    }
}
