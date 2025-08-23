package org.mandarin.booking.app;

import org.mandarin.booking.domain.member.TokenHolder;

public interface TokenUtils {
    TokenHolder generateToken(String refreshToken);

    TokenHolder generateToken(String userId, String nickName);

    String getClaim(String token, String claimName);

    void validateToken(String token);
}
