package org.mandarin.booking.app.port;

import org.mandarin.booking.TokenHolder;

public interface AuthUseCase {
    TokenHolder login(String userId, String password);
    TokenHolder reissue(String refreshToken);
}
