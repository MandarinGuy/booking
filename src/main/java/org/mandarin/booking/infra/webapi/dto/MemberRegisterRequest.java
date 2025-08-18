package org.mandarin.booking.infra.webapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberRegisterRequest(
        @NotBlank(message = "Nickname cannot be blank")
        String nickName,

        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Password cannot be blank")
        String password,

        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email format")
        @NotBlank(message = "Email cannot be blank")
        String email) {
}
