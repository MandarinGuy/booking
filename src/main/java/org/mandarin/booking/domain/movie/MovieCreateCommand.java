package org.mandarin.booking.domain.movie;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.mandarin.booking.domain.movie.Movie.Genre;
import org.mandarin.booking.domain.movie.Movie.Rating;

@Getter
public class MovieCreateCommand {
    private final String title;
    private final Genre genre;
    private final int runtimeMinutes;
    private final String director;
    private final String synopsis;
    private final String posterUrl;
    private final LocalDate releaseDate;
    private final Rating rating;
    private final Set<String> casts;

    private MovieCreateCommand(String title, Genre genre, int runtimeMinutes, String director, String synopsis,
                              String posterUrl, LocalDate releaseDate, Rating rating, Set<String> casts) {
        this.title = title;
        this.genre = genre;
        this.runtimeMinutes = runtimeMinutes;
        this.director = director;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.casts = casts;
    }

    public static MovieCreateCommand from(MovieRegisterRequest request) {

        return new MovieCreateCommand(
                request.title(),
                Genre.valueOf(request.genre()),
                request.runtimeMinutes(),
                request.director(),
                request.synopsis(),
                request.posterUrl(),
                LocalDate.parse(request.releaseDate()),
                Rating.valueOf(request.rating()),
                new HashSet<>(request.casts())
        );
    }
}
