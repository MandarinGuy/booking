package org.mandarin.booking.domain.hall;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SectionRegisterRequest(
        @NotBlank
        String sectionName,
        List<SeatRegisterRequest> seats) {

}
