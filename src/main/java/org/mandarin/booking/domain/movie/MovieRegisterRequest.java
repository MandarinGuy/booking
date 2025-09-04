package org.mandarin.booking.domain.movie;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record MovieRegisterRequest(
        @NotBlank(message = "Title must not be blank")
        String title,

        @NotBlank(message = "Director must not be blank")
        String director,

        @NotNull(message = "Runtime minutes must not be null")
        @Min(value = 0, message = "Runtime minutes must be non-negative")
        Integer runtimeMinutes,

        @NotBlank(message = "Genre must not be blank")
        String genre,

        @NotBlank(message = "Release date must not be blank")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "releaseDate must be yyyy-MM-dd")
        String releaseDate,

        @NotBlank(message = "Rating must not be blank")
        String rating,

        String synopsis,
        String posterUrl,
        List<String> casts) {
}
