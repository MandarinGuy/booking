package org.mandarin.booking.domain.service;

public interface TokenProvider {
    String generateToken(String userId, String nickName, long expiration);
}
