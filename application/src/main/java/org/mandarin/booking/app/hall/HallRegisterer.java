package org.mandarin.booking.app.hall;

import org.mandarin.booking.domain.hall.HallRegisterRequest;
import org.mandarin.booking.domain.hall.HallRegisterResponse;

public interface HallRegisterer {
    HallRegisterResponse register(String userId, HallRegisterRequest request);
}
