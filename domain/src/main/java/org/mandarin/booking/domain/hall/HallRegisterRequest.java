package org.mandarin.booking.domain.hall;

import java.util.List;

public record HallRegisterRequest(String hallName, List<SectionRegisterRequest> sectionRegisterRequests) {
}

