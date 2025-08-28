package org.mandarin.booking.app.port;

import org.mandarin.booking.domain.movie.MovieRegisterRequest;
import org.mandarin.booking.domain.movie.MovieRegisterResponse;

public interface MovieRegisterer {
    MovieRegisterResponse register(MovieRegisterRequest request);
}
