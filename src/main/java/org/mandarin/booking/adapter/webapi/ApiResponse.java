package org.mandarin.booking.adapter.webapi;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse {
    protected boolean success = true;
    private final LocalDateTime timestamp = LocalDateTime.now();
    protected String status;
}


