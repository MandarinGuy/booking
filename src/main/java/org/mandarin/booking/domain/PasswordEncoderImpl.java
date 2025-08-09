package org.mandarin.booking.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderImpl implements PasswordEncoder {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String encode(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || rawPassword.isEmpty() || encodedPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password and encoded password cannot be null or empty");
        }
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
