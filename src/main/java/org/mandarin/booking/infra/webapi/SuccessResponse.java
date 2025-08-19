package org.mandarin.booking.infra.webapi;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SuccessResponse<T> extends ApiResponse<T> {

    public SuccessResponse(ApiStatus status, T data) {
        this.status = status;
        this.data = data;
    }
}
