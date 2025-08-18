package org.mandarin.booking.infra.webapi.dto;

public record TokenHolder(String accessToken, String refreshToken) {
}
