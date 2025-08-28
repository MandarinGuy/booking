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


    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor_name")
    private Set<String> casts = new HashSet<>();

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
                  Set<String> casts) {

        this.title = title;
        this.director = director;
        this.runtimeMinutes = runtimeMinutes;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.casts.addAll(casts);
    }

    public static Movie create(MovieCreateCommand command) {
        return new Movie(command.getTitle(), command.getDirector(), command.getRuntimeMinutes(), command.getGenre(),
                command.getReleaseDate(), command.getRating(), command.getSynopsis(), command.getPosterUrl(), command.getCast());
    }

    public enum Genre {
        ACTION, DRAMA, COMEDY, THRILLER, ROMANCE, SF, FANTASY, HORROR, ANIMATION, DOCUMENTARY, ETC
    }

    public enum Rating {
        ALL, AGE12, AGE15, AGE18
    }
}
