package org.mandarin.booking.domain.hall;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record HallRegisterRequest(
        @NotBlank
        String hallName,
        @Size(min = 1)
        List<SectionRegisterRequest> sectionRegisterRequests) {
}

