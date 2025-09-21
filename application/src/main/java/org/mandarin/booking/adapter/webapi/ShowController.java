package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.app.show.ShowFetcher;
import org.mandarin.booking.app.show.ShowRegisterer;
import org.mandarin.booking.domain.EnumRequest;
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
record ShowController(ShowRegisterer showRegisterer, ShowFetcher showFetcher) {

    @GetMapping
    @Valid
    SliceView<ShowResponse> inquire(@RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
                                    @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(value = 100) Integer size,
                                    @RequestParam(required = false) @EnumRequest(value = Type.class, nullable = true) String type,
                                    @RequestParam(required = false) @EnumRequest(value = Rating.class, nullable = true) String rating,
                                    @RequestParam(required = false) String q,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return showFetcher.fetchShows(page, size, type, rating, q, from, to);
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

