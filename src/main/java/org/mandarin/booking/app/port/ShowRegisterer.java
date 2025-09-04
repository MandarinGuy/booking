package org.mandarin.booking.app.port;

import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;

public interface ShowRegisterer {
    ShowRegisterResponse register(ShowRegisterRequest request);
}

