package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.app.show.ShowRegisterer;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.mandarin.booking.domain.show.ShowResponse;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/show")
record ShowController(ShowRegisterer showRegisterer) {

    @GetMapping
    SliceView<ShowResponse> inquire(@RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String rating,
                                    @RequestParam(required = false) String q,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return new SliceView<>(List.of(
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now()),
                new ShowResponse(0L, "", Type.MUSICAL, Rating.ALL, "", "", LocalDate.now(), LocalDate.now())
        ),
                0);
    }

    @PostMapping
    ShowRegisterResponse register(@RequestBody @Valid ShowRegisterRequest request) {
        return showRegisterer.register(request);
    }

    @PostMapping("/schedule")
    ShowScheduleRegisterResponse registerSchedule(@RequestBody @Valid ShowScheduleRegisterRequest request) {
        return showRegisterer.registerSchedule(request);
    }
}

