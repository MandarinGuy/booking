package org.mandarin.booking.adapter.webapi;

public interface AuthUseCase {
    TokenHolder login(AuthRequest request);
}
