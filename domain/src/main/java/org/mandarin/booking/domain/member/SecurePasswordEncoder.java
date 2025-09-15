package org.mandarin.booking.domain.member;

import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public interface SecurePasswordEncoder {
    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
