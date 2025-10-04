package org.mandarin.booking.adapter.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BCryptSecurePasswordEncoderTest {
    BCryptSecurePasswordEncoder securePasswordEncoder;

    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return Base64.getEncoder().encodeToString(rawPassword.toString().getBytes());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return Base64.getEncoder().encodeToString(rawPassword.toString().getBytes()).equals(encodedPassword);
            }
        };

        securePasswordEncoder = new BCryptSecurePasswordEncoder(passwordEncoder);
    }

    @Test
    void encode() {
        String rawPassword = "password123";
        String encodedPassword = securePasswordEncoder.encode(rawPassword);
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(securePasswordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    void matches() {
        String rawPassword = "password123";
        String encodedPassword = securePasswordEncoder.encode(rawPassword);
        assertThat(securePasswordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}
