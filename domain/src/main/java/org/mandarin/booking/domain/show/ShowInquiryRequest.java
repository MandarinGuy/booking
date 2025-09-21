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

        String q,

        @DateTimeFormat(iso = ISO.DATE)
        LocalDate from,

        @DateTimeFormat(iso = ISO.DATE)
        LocalDate to
) {
    public ShowInquiryRequest {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
    }
}
