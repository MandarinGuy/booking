package org.mandarin.booking.domain.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record MovieRegisterRequest(
        @NotBlank(message = "Title must not be blank")
        String title,

        @NotBlank(message = "Director must not be blank")
        String director,

        @NotNull(message = "Runtime minutes must not be null")
        Integer runtimeMinutes,

        @NotBlank(message = "Genre must not be blank")
        String genre,

        @NotNull(message = "Release date must not be blank")
        LocalDate releaseDate,

        @NotBlank(message = "Rating must not be blank")
        String rating,

        String synopsis,
        String posterUrl,
        List<String> casts) {
}
