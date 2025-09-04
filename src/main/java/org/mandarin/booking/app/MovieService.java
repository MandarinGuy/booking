package org.mandarin.booking.app;

import static java.util.Objects.requireNonNull;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.persist.MovieCommandRepository;
import org.mandarin.booking.app.port.MovieRegisterer;
import org.mandarin.booking.domain.movie.Movie;
import org.mandarin.booking.domain.movie.MovieCreateCommand;
import org.mandarin.booking.domain.movie.MovieRegisterRequest;
import org.mandarin.booking.domain.movie.MovieRegisterResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService implements MovieRegisterer {
    private final MovieCommandRepository commandRepository;
    @Override
    public MovieRegisterResponse register(MovieRegisterRequest request) {
        var command = MovieCreateCommand.from(request);
        var movie = Movie.create(command);
        var savedMovie = commandRepository.insert(movie);
        return new MovieRegisterResponse(requireNonNull(savedMovie.getId()));
    }
}
