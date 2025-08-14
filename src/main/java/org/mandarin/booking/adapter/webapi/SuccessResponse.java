package org.mandarin.booking.adapter.webapi;

import lombok.Getter;

@Getter
public class SuccessResponse<T> extends ApiResponse {
    private T data;

    public SuccessResponse(String status, T data) {
        super();
        this.status = status;
        this.data = data;
    }
}
