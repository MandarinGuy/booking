package org.mandarin.booking.adapter.webapi;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Password cannot be blank")
        String password) {
}
