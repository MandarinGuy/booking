package org.mandarin.booking.adapter.webapi;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullUnmarked;

@Getter
@NoArgsConstructor
@NullUnmarked
public abstract class ApiResponse<T> {
    private final LocalDateTime timestamp = LocalDateTime.now();
    protected ApiStatus status;
    protected T data;
}


