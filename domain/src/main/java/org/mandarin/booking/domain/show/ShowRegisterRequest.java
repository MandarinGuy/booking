package org.mandarin.booking.domain.show;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mandarin.booking.Currency;
import org.mandarin.booking.domain.EnumRequest;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;

public record ShowRegisterRequest(
        @NotNull(message = "hall id is required")
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
        LocalDate performanceEndDate,

        @NotBlank(message = "currency is required")
        @EnumRequest(value = Currency.class, message = "invalid currency")
        String currency,

        @NotNull(message = "ticketGrades are required")
        @NotEmpty(message = "ticketGrades must not be empty")
        List<@Valid TicketGradeRequest> ticketGrades
) {
    @AssertTrue(message = "ticketGrade names must be unique")
    public boolean hasUniqueTicketGradeNames() {
        Set<String> names = ticketGrades.stream().map(TicketGradeRequest::name).collect(Collectors.toSet());
        return names.size() == ticketGrades.size();
    }

    public record TicketGradeRequest(
            @NotBlank(message = "ticketGrade name is required")
            String name,
            @Positive(message = "basePrice must be positive")
            Integer basePrice
    ) {
    }
}
