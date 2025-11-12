package org.mandarin.booking.app.show;

import org.mandarin.booking.domain.show.SeatsResponse;

public interface InventoryReader {
    SeatsResponse fetchSeats(Long scheduleId);
}

