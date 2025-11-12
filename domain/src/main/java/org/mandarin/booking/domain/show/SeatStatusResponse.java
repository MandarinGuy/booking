package org.mandarin.booking.domain.show;

public record SeatStatusResponse(
        Long seatId,
        Long gradeId,
        SeatStatus status
) {
}
