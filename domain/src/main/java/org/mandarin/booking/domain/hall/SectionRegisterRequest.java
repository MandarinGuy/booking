package org.mandarin.booking.domain.hall;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SectionRegisterRequest(
        @NotBlank
        String sectionName,
        @Size(min = 1)
        List<SeatRegisterRequest> seats) {

}
