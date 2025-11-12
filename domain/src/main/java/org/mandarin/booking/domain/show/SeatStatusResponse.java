package org.mandarin.booking.domain.show;

public record SeatStatusResponse(
        Long seatId,
        Long gradeId,
        SeatAvailability status
) {
}
