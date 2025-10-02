package org.mandarin.booking.domain.hall;

import jakarta.validation.constraints.NotBlank;

public record SeatRegisterRequest(
        @NotBlank
        String rowNumber,
        @NotBlank
        String seatNumber) {
}
