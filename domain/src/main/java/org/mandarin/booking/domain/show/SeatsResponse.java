package org.mandarin.booking.domain.show;

import java.util.List;

public record SeatsResponse(
        List<SeatStatusResponse> contents
) {
}

