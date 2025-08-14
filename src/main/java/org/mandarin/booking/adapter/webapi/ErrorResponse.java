package org.mandarin.booking.adapter.webapi;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse extends ApiResponse {
    private String message;

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.success = false;
    }
}
