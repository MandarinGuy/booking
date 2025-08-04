package org.mandarin.booking.domain;

public record MemberRegisterRequest(String nickName, String userId, String passwordHash, String email) {
}
