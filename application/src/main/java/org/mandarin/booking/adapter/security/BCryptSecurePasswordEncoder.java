package org.mandarin.booking.adapter.security;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptSecurePasswordEncoder implements SecurePasswordEncoder {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
