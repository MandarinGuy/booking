package org.mandarin.booking.app.show;

import jakarta.validation.Valid;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/show")
record ShowController(ShowRegisterer showRegisterer) {

    @PostMapping
    ShowRegisterResponse register(@RequestBody @Valid ShowRegisterRequest request) {
        return showRegisterer.register(request);
    }

    @PostMapping("/schedule")
    ShowScheduleRegisterResponse registerSchedule(@RequestBody @Valid ShowScheduleRegisterRequest request) {
        return showRegisterer.registerSchedule(request);
    }
}

