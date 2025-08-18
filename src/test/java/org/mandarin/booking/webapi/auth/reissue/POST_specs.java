package org.mandarin.booking.webapi.auth.reissue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.JwtTestUtils.assertJwtFormat;
import static org.mandarin.booking.infra.webapi.ApiStatus.SUCCESS;
import static org.mandarin.booking.infra.webapi.ApiStatus.UNAUTHORIZED;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.infra.webapi.dto.ReissueRequest;
import org.mandarin.booking.infra.webapi.dto.TokenHolder;
import org.mandarin.booking.app.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("POST /api/auth/reissue")
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
                        request
                )
                .assertSuccess(TokenHolder.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
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
                        request
                )
                .assertSuccess(TokenHolder.class);

        // Assert
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().accessToken()).isNotEmpty();
        assertThat(response.getData().refreshToken()).isNotEmpty();
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
                        request
                )
                .assertSuccess(TokenHolder.class);

        // Assert
        var accessToken = response.getData().accessToken();
        var refreshToken = response.getData().refreshToken();

        assertJwtFormat(accessToken);
        assertJwtFormat(refreshToken);
    }

    @Test
    void 요청_토큰의_서명이_잘못된_경우_401_Unauthorized가_발생한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        testUtils.insertDummyMember(generateUserId(), generatePassword());
        var invalidRefresh = "invalid_refresh_token";
        var request = new ReissueRequest(invalidRefresh);

        // Act
        var response = testUtils.post(
                        "/api/auth/reissue",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    private static String getValidRefreshToken(TokenProvider tokenProvider, String userId, String nickName) {
        return tokenProvider.generateToken(userId, nickName).refreshToken();
    }
}
