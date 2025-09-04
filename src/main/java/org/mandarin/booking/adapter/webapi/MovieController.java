package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import org.mandarin.booking.app.port.MovieRegisterer;
import org.mandarin.booking.domain.movie.MovieRegisterRequest;
import org.mandarin.booking.domain.movie.MovieRegisterResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movie")
public record MovieController(MovieRegisterer movieRegisterer) {

    @PostMapping
    public MovieRegisterResponse register(@RequestBody @Valid MovieRegisterRequest request) {
        return movieRegisterer.register(request);
    }
}
