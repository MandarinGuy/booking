package org.mandarin.booking.app.port;

import org.mandarin.booking.adapter.webapi.AuthRequest;
import org.mandarin.booking.adapter.webapi.TokenHolder;

public interface AuthUseCase {
    TokenHolder login(AuthRequest request);
}
