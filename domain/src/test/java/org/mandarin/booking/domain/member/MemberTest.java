package org.mandarin.booking.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;

class MemberTest {
    private static final String MAIL = "test@test.com";
    private static final String HASHED_PASSWORD = "hashedPassword";
    private static final String TEST_USER = "testUser";
    private static final String TEST_NICK = "testNick";
    MemberCreateCommand createCommand;

    @Test
    void testCreation() {
        createCommand = new MemberCreateCommand(TEST_NICK, TEST_USER, HASHED_PASSWORD, MAIL);
        SecurePasswordEncoder securePasswordEncoder = mock(SecurePasswordEncoder.class);
        var member = Member.create(createCommand, securePasswordEncoder);

        assertThat(member).isNotNull();
    }
}
