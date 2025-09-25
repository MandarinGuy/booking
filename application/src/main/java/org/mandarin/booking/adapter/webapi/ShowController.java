package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.app.show.ShowFetcher;
import org.mandarin.booking.app.show.ShowRegisterer;
import org.mandarin.booking.domain.show.ShowDetailResponse;
import org.mandarin.booking.domain.show.ShowInquiryRequest;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.mandarin.booking.domain.show.ShowResponse;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/show")
record ShowController(ShowRegisterer showRegisterer, ShowFetcher showFetcher) {

    @GetMapping
    SliceView<ShowResponse> inquire(@Valid ShowInquiryRequest req) {
        return showFetcher.fetchShows(req.page(), req.size(), req.type(), req.rating(), req.q(), req.from(), req.to());
    }

    @GetMapping("/{showId}")
    ShowDetailResponse inquireDetail(@PathVariable
                                     @Positive(message = "show Id는 음수일 수 없습니다.")
                                     Long showId) {
        return showFetcher.fetchShowDetail(showId);
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

