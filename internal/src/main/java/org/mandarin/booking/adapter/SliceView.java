package org.mandarin.booking.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SliceView<T>(
        @JsonProperty("contents") List<T> contents,
        @JsonProperty("page") int page,
        @JsonProperty("size") int size,
        @JsonProperty("hasNext") boolean hasNext
) {

    public SliceView(List<T> contents, int page) {
        this(contents, page, contents.size(), false);
    }
}
