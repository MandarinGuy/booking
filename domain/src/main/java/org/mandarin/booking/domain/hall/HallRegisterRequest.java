package org.mandarin.booking.domain.hall;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record HallRegisterRequest(
        @NotBlank
        String hallName,
        List<SectionRegisterRequest> sectionRegisterRequests) {
}

