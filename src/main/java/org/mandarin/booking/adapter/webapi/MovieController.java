package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import org.mandarin.booking.app.Log;
import org.mandarin.booking.domain.movie.MovieRegisterRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Log
    @PostMapping
    public void register(@RequestBody @Valid MovieRegisterRequest request) {
        // Movie registration logic would go here

    }
}
