package org.mandarin.booking.domain.show;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.mandarin.booking.domain.EnumRequest;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;

public record ShowRegisterRequest(
        @NotNull(message = "venue id is required")
        Long hallId,

        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "type is required")
        @EnumRequest(value = Type.class, message = "invalid type")
        String type,

        @NotBlank(message = "rating is required")
        @EnumRequest(value = Rating.class, message = "invalid rating")
        String rating,

        @NotBlank(message = "synopsis is required")
        String synopsis,

        @NotBlank(message = "posterUrl is required")
        String posterUrl,

        @NotNull(message = "performance start date is required")
        @FutureOrPresent(message = "performance start date must be today or future")
        LocalDate performanceStartDate,

        @NotNull(message = "performance end date is required")
        LocalDate performanceEndDate
) {
}

