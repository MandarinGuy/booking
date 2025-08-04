package org.mandarin.booking.domain;

import jakarta.validation.constraints.NotBlank;

public record MemberRegisterRequest(
        @NotBlank(message = "Nickname cannot be blank")
        String nickName,

        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Password hash cannot be blank")
        String passwordHash,

        @NotBlank(message = "Email cannot be blank")
        String email) {
}
