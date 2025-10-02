package org.mandarin.booking.adapter.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BCryptSecurePasswordEncoderTest {
    @Test
    void encode(@Autowired BCryptSecurePasswordEncoder encoder) {
        String rawPassword = "password123";
        String encodedPassword = encoder.encode(rawPassword);
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    void matches(@Autowired BCryptSecurePasswordEncoder encoder) {
        String rawPassword = "password123";
        String encodedPassword = encoder.encode(rawPassword);
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}
