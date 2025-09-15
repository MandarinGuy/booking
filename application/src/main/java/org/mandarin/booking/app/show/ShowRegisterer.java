package org.mandarin.booking.app.show;

import jakarta.validation.Valid;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;

public interface ShowRegisterer {
    ShowRegisterResponse register(ShowRegisterRequest request);

    ShowScheduleRegisterResponse registerSchedule(@Valid ShowScheduleRegisterRequest request);
}

