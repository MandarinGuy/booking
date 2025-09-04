package org.mandarin.booking.app.persist;

import org.mandarin.booking.domain.movie.Movie;
import org.springframework.data.repository.Repository;

public interface MovieRepository extends Repository<Movie, Long> {
    Movie save(Movie movie);
}
