package org.mandarin.booking.webapi.auth.reissue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.JwtTestUtils.assertJwtFormat;
import static org.mandarin.booking.JwtTestUtils.getExpiration;
import static org.mandarin.booking.MemberAuthority.USER;
import static org.mandarin.booking.adapter.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.ApiStatus.UNAUTHORIZED;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.TokenHolder;
import org.mandarin.booking.adapter.TokenUtils;
import org.mandarin.booking.domain.member.ReissueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

@IntegrationTest
@DisplayName("POST /api/auth/reissue")
public class POST_specs {
    @Test
    void 올바른_refresh_token으로_요청하면_200을_응답한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var validRefreshToken = testUtils.getValidRefreshToken();
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
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var validRefreshToken = testUtils.getValidRefreshToken();
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
            @Value("${jwt.token.secret}") String key
    ) {
        // Arrange
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes());
        var validRefreshToken = testUtils.getValidRefreshToken();
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

        var accessTokenExpiration = getExpiration(secretKey, accessToken);
        var refreshTokenExpiration = getExpiration(secretKey, refreshToken);

        assertThat(accessTokenExpiration).isAfter(new Date());
        assertThat(refreshTokenExpiration).isAfter(new Date());
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

    @Test
    void 요청_body가_누락된_경우_400_Bad_Request가_발생한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new ReissueRequest(null);

        // Act
        var response = testUtils.post(
                        "/api/auth/reissue",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하지_않는_사용자의_refresh_token을_요청하면_401_Unauthorize가_발생한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TokenUtils tokenUtils
    ) {
        // Arrange
        var validRefreshToken = tokenUtils.generateToken(generateUserId(), generateNickName(), List.of(USER))
                .refreshToken();
        var request = new ReissueRequest(validRefreshToken);

        // user 생성 안함

        // Act
        var response = testUtils.post(
                        "/api/auth/reissue",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Nested
    @TestPropertySource(properties = "jwt.token.refresh=100")
    class ReissueShortToken {
        @Test
        void 만료된_refresh_token으로_요청하면_401_Unauthorize가_발생한다(
                @Autowired IntegrationTestUtils testUtils
        ) throws InterruptedException {
            // Arrange
            var request = new ReissueRequest(
                    testUtils.getValidRefreshToken());
            Thread.sleep(100); //TODO 2025 08 18 16:47:00 : 시간 의존적 코드가 테스트 속도에 영향을 미치지 않도록 개선 필요

            // Act
            var response = testUtils.post(
                            "/api/auth/reissue",
                            request
                    )
                    .assertFailure();

            // Assert
            assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
        }
    }
}
