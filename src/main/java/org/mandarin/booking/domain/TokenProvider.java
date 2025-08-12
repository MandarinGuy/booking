package org.mandarin.booking.domain;

public interface TokenProvider {
    String generateToken(String userId, String nickName, long expiration);
}
