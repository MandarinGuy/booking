package org.mandarin.booking.domain.member;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
        @NotBlank(message = "Refresh token must not be blank")
        String refreshToken) {
}
