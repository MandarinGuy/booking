package org.mandarin.booking.domain.hall;

import java.util.List;

public record SectionRegisterRequest(String sectionName, List<SeatRegisterRequest> seats) {

}
