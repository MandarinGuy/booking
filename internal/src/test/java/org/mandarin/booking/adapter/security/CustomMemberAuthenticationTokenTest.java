package org.mandarin.booking.adapter.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.MemberAuthority.USER;

import java.util.List;
import org.junit.jupiter.api.Test;

class CustomMemberAuthenticationTokenTest {

    @Test
    void getCredentials() {
        var toke = new CustomMemberAuthenticationToken("user", List.of(USER));
        assertThat(toke.getCredentials()).isNull();
    }
}
