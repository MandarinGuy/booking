package org.mandarin.booking.domain.show;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record ShowRegisterRequest(
        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "type is required")
        @Pattern(regexp = "MUSICAL|PLAY|CONCERT|OPERA|DANCE|CLASSICAL|ETC", message = "invalid type")
        String type,

        @NotBlank(message = "rating is required")
        @Pattern(regexp = "ALL|AGE12|AGE15|AGE18", message = "invalid rating")
        String rating,

        @NotBlank(message = "synopsis is required")
        String synopsis,

        @NotBlank(message = "posterUrl is required")
        String posterUrl,

        @NotNull(message = "performanceStartDate is required")
        @FutureOrPresent(message = "performanceStartDate must be today or future")
        LocalDate performanceStartDate,

        @NotNull(message = "performanceEndDate is required")
        LocalDate performanceEndDate
) {
}

