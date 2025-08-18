package org.mandarin.booking.app;

import org.mandarin.booking.infra.webapi.dto.TokenHolder;

public interface TokenProvider {
    TokenHolder generateToken(String refreshToken);

    TokenHolder generateToken(String userId, String nickName);

    void validateToken(String token);
}
