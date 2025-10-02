package org.mandarin.booking.app.hall;

public interface HallValidator {
    void checkHallExistByHallId(Long hallId);

    void checkHallExistByHallName(String hallName);
}
