package org.mandarin.booking.infra.webapi.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
        @NotBlank(message = "Refresh token must not be blank")
        String refreshToken) {
}
