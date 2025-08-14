package org.mandarin.booking.webapi.auth.reissue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.JwtTestUtils.assertJwtFormat;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.app.TokenProvider;
import org.mandarin.booking.adapter.webapi.dto.ReissueRequest;
import org.mandarin.booking.adapter.webapi.dto.TokenHolder;
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
        var validRefreshToken = getValidRefreshToken(tokenProvider, userId, nickName);
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

    @Test
    void 올바른_refresh_token으로_요청하면_새로운_access_token과_refresh_token을_발급해_응답한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TokenProvider tokenProvider
    ) {
        // Arrange
        var userId = generateUserId();
        var nickName = generateNickName();
        testUtils.insertDummyMember(userId, generatePassword());
        var validRefreshToken = getValidRefreshToken(tokenProvider, userId, nickName);
        var request = new ReissueRequest(validRefreshToken);

        // Act
        var response = testUtils.post(
                "/api/auth/reissue",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotEmpty();
        assertThat(response.getBody().refreshToken()).isNotEmpty();
    }
    
    @Test
    void 응답받은_access_toke과_refresh_toke은_유효한_JWT_형식이다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TokenProvider tokenProvider
    ) {
        // Arrange
        var userId = generateUserId();
        var nickName = generateNickName();
        testUtils.insertDummyMember(userId, generatePassword());
        var validRefreshToken = getValidRefreshToken(tokenProvider, userId, nickName);
        var request = new ReissueRequest(validRefreshToken);

        // Act
        var response = testUtils.post(
                "/api/auth/reissue",
                request,
                TokenHolder.class
        );

        // Assert
        var accessToken = response.getBody().accessToken();
        var refreshToken = response.getBody().refreshToken();

        assertJwtFormat(accessToken);
        assertJwtFormat(refreshToken);
    }

    private static String getValidRefreshToken(TokenProvider tokenProvider, String userId, String nickName) {
        return tokenProvider.generateToken(userId, nickName).refreshToken();
    }
}
