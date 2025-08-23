package org.mandarin.booking.adapter.webapi;

import com.fasterxml.jackson.annotation.JsonProperty;


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

