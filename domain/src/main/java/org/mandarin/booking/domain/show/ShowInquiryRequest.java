package org.mandarin.booking.domain.show;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import org.jspecify.annotations.Nullable;
import org.mandarin.booking.domain.EnumRequest;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record ShowInquiryRequest(
        @Min(0) @Nullable
        Integer page,

        @Min(1) @Nullable @Max(100)
        Integer size,

        @EnumRequest(value = Type.class, nullable = true)
        String type,

        @EnumRequest(value = Rating.class, nullable = true)
        String rating,

        @Nullable
        String q,

        @DateTimeFormat(iso = ISO.DATE)
        @Nullable
        LocalDate from,

        @DateTimeFormat(iso = ISO.DATE)
        @Nullable
        LocalDate to
) {
    public ShowInquiryRequest {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }

        if ((from != null && to != null) && from.isAfter(to)) {
            throw new ShowException("BAD_REQUEST", "from 는 to 보다 과거만 가능합니다.");
        }
        q = (q == null) ? null : q.trim();
        if (q != null && q.isEmpty()) {
            throw new ShowException("BAD_REQUEST", "q는 공백일 수 없습니다.");
        }
    }
}
