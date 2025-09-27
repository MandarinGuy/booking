package org.mandarin.booking.app.show;

import java.time.LocalDate;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.domain.show.ShowDetailResponse;
import org.mandarin.booking.domain.show.ShowResponse;

public interface ShowFetcher {
    SliceView<ShowResponse> fetchShows(Integer page, Integer size, String type, String rating, String q,
                                       LocalDate from, LocalDate to);

    ShowDetailResponse fetchShowDetail(Long showId);
}
