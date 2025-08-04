package org.mandarin.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void testRegisterWithNullRequest() {
        request = new MemberRegisterRequest(null, TEST_USER, HASHED_PASSWORD, MAIL);

        assertThatThrownBy(
                () -> Member.register(request)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Nick cannot be null");

        request = new MemberRegisterRequest(TEST_NICK, null, HASHED_PASSWORD, MAIL);

        assertThatThrownBy(
                () -> Member.register(request)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("UserId cannot be null");

        request = new MemberRegisterRequest(TEST_NICK, TEST_USER, null, MAIL);
        assertThatThrownBy(
                () -> Member.register(request)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("PasswordHash cannot be null");

        request = new MemberRegisterRequest(TEST_NICK, TEST_USER, HASHED_PASSWORD, null);
        assertThatThrownBy(
                () -> Member.register(request)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Email cannot be null");
    }
}
