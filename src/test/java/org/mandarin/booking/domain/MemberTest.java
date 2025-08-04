package org.mandarin.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MemberTest {
    private static final String MAIL = "test@test.com";
    private static final String HASHED_PASSWORD = "hashedPassword";
    private static final String TEST_USER = "testUser";
    private static final String TEST_NICK = "testNick";
    MemberRegisterRequest request;

    @Test
    void testRegister() {
        request = new MemberRegisterRequest(TEST_NICK, TEST_USER, HASHED_PASSWORD, MAIL);
        var register = Member.register(request);

        assertThat(register).isNotNull();
    }
}
