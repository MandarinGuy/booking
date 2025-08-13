package org.mandarin.booking.webapi.auth.reissue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.app.TokenProvider;
import org.mandarin.booking.domain.member.ReissueRequest;
import org.mandarin.booking.domain.member.TokenHolder;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class POST_specs {
    @Test
    void 올바른_refresh_token으로_요청하면_200을_응답한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TokenProvider tokenProvider
    ) {
        // Arrange
        var userId = generateUserId();
        var nickName = generateNickName();
        testUtils.insertDummyMember(userId, generatePassword());
        var validRefreshToken = tokenProvider.generateToken(userId, nickName, 1200000L);
        var request = new ReissueRequest(validRefreshToken);

        // Act
        var response = testUtils.post(
                "/api/auth/reissue",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
}
