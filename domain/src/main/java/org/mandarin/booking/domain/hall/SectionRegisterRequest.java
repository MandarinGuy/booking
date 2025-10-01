package org.mandarin.booking.domain.hall;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record SectionRegisterRequest(
        @NotBlank
        String sectionName,
        @Size(min = 1)
        @Valid
        List<SeatRegisterRequest> seats) {
    @AssertFalse(message = "Duplicate seats are not allowed")
    public boolean hasDuplicateSeats() {
        Set<SeatRegisterRequest> seen = new HashSet<>();
        return seats.stream().anyMatch(seat -> !seen.add(seat));
    }
}
