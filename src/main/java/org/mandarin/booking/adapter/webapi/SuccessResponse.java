package org.mandarin.booking.adapter.webapi;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SuccessResponse<T> extends ApiResponse<T> {

    public SuccessResponse(ApiStatus status, T data) {
        this.status = status;
        this.data = data;
    }
}
