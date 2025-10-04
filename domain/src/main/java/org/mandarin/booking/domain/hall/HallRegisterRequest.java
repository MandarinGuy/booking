package org.mandarin.booking.domain.hall;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record HallRegisterRequest(
        @NotBlank
        String hallName,
        @Size(min = 1, message = "At least one section is required")
        @Valid
        List<SectionRegisterRequest> sections) {
    @AssertFalse(message = "Duplicate section names are not allowed")
    public boolean hasDuplicateSectionNames() {
        return sections.stream()
                       .map(SectionRegisterRequest::sectionName)
                       .distinct()
                       .count() != sections.size();
    }
}

