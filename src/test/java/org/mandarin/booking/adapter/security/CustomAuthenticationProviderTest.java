package org.mandarin.booking.adapter.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@IntegrationTest
class CustomAuthenticationProviderTest {
    @Autowired
    CustomAuthenticationProvider provider;

    @Test
    void supports() {
        var isSupported = provider.supports(CustomMemberAuthenticationToken.class);
        assertThat(isSupported).isTrue();
    }

    @Test
    void supportsFailure(){
        var isSupported = provider.supports(String.class);
        assertThat(isSupported).isFalse();
    }

    @Test
    void shouldNotAuthenticateNonCustomToken() {
        var exception = assertThrows(RuntimeException.class,
                () -> provider.authenticate(new UsernamePasswordAuthenticationToken("user", "pass")));
        assertTrue(exception.getMessage().contains("Unsupported authentication type"));
    }
}
