package org.mandarin.booking.app.hall;

import java.util.List;

public interface HallValidator {
    void checkHallExistByHallId(Long hallId);

    void checkHallExistByHallName(String hallName);

    void checkHallExistBySectionId(Long hallId, Long sectionId);

    void checkHallInvalidSeatIds(Long sectionId, List<Long> seatIds);
}
