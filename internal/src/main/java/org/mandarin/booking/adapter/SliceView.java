package org.mandarin.booking.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public record SliceView<T>(
        @JsonProperty("contents") List<T> contents,
        @JsonProperty("page") int page,
        @JsonProperty("size") int size,
        @JsonProperty("hasNext") boolean hasNext
) {
}
