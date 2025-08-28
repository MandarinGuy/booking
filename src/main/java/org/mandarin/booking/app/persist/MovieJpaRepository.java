package org.mandarin.booking.app.persist;

import org.mandarin.booking.domain.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieJpaRepository extends JpaRepository<Movie, Long> {
}
