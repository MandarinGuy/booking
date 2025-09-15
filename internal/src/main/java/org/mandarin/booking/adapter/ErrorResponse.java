package org.mandarin.booking.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;


@ToString
public class ErrorResponse extends ApiResponse<String> {

    public ErrorResponse(ApiStatus status, String message) {
        this.status = status;
        this.data = message;
    }

    @Override
    @JsonProperty("message")
    public String getData() {
        return super.getData();
    }
}

