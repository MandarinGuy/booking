package org.mandarin.booking.adapter.webapi;

import static org.mandarin.booking.domain.show.ShowDetailResponse.HallResponse;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
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
    ShowDetailResponse inquireDetail(Long showId) {
        return new ShowDetailResponse(
                showId,
                "title",
                "type",
                "rating",
                "synopsis",
                "posterUrl",
                LocalDate.now(),
                LocalDate.now(),
                new HallResponse(1L, "hallName"),
                List.of()
        );
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

