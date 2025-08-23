package org.mandarin.booking.domain.movie;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
public class Movie extends AbstractEntity {
    private String title;

    private String director;

    private Integer runtimeMinutes;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    private LocalDate releaseDate;

    private String synopsis;

    private String posterUrl;


    private Set<String> cast = new LinkedHashSet<>();

    protected Movie() {
    }

    private Movie(String title,
                  String director,
                  Integer runtimeMinutes,
                  Genre genre,
                  LocalDate releaseDate,
                  Rating rating,
                  String synopsis,
                  String posterUrl,
                  Set<String> cast) {

        this.title = title;
        this.director = director;
        this.runtimeMinutes = runtimeMinutes;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        if (cast != null) {
            this.cast.addAll(cast);
        }
    }

    public static Movie create(String title,
                               String director,
                               Integer runtimeMinutes,
                               Genre genre,
                               LocalDate releaseDate,
                               Rating rating,
                               @Nullable
                               String synopsis,
                               @Nullable
                               String posterUrl,
                               @Nullable
                               Set<String> cast) {
        if (runtimeMinutes < 0) {
            throw new IllegalArgumentException("Runtime minutes cannot be negative");//TODO 2025 08 19 11:35:28 : custom exception
        }

        return new Movie(title, director, runtimeMinutes, genre, releaseDate, rating, synopsis, posterUrl, cast);
    }

    public enum Genre {
        ACTION, DRAMA, COMEDY, THRILLER, ROMANCE, SF, FANTASY, HORROR, ANIMATION, DOCUMENTARY, ETC
    }

    public enum Rating {
        ALL, AGE12, AGE15, AGE18
    }
}
