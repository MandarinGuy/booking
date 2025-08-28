package org.mandarin.booking.app.persist;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.movie.Movie;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class MovieCommandRepository {
    private final MovieJpaRepository jpaRepository;

    public Movie insert(Movie movie){
        return jpaRepository.save(movie);
    }
}
