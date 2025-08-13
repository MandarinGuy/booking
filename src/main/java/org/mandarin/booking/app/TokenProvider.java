package org.mandarin.booking.app;

public interface TokenProvider {
    String generateToken(String userId, String nickName, long expiration);
}
