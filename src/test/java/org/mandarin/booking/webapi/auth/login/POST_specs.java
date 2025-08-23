package org.mandarin.booking.webapi.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.JwtTestUtils.assertJwtFormat;
import static org.mandarin.booking.JwtTestUtils.getExpiration;
import static org.mandarin.booking.JwtTestUtils.getTokenClaims;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;
import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.webapi.ApiStatus.UNAUTHORIZED;

import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.app.persist.MemberQueryRepository;
import org.mandarin.booking.domain.member.AuthRequest;
import org.mandarin.booking.domain.member.TokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@IntegrationTest
@DisplayName("POST /api/auth/login")
public class POST_specs {
    @Test
    void 올바른_요청을_보내면_200_OK_상태코드를_반환한다(
            @Autowired IntegrationTestUtils integrationUtils
    ) {
        // Arrange
        var request = new AuthRequest(generateUserId(), generatePassword());

        // save member
        integrationUtils.insertDummyMember(request.userId(), request.password());

        // Act
        var response = integrationUtils.post(
                        "/api/auth/login",
                        request
                )
                .assertSuccess(TokenHolder.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.auth.login.POST_specs#blankUserIdRequests")
    void 요청_본문의_userId가_누락된_경우_400_Bad_Request_상태코드를_반환한다(
            // Arrange
            AuthRequest request,
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Act
        var response = testUtils.post(
                        "/api/auth/login",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.auth.login.POST_specs#blankPasswordRequests")
    void 요청_본문의_password가_누락된_경우_400_Bad_Request_상태코드를_반환한다(
            // Arrange
            AuthRequest request,
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Act
        var response = testUtils.post(
                        "/api/auth/login",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하지_않는_userId_비밀번호로_요청하면_401_Unauthorized_상태코드를_반환한다(
            @Autowired IntegrationTestUtils integrationUtils
    ) {
        // Arrange
        var request = new AuthRequest("nonExistentUser", generatePassword());

        // don't need to save member in the database

        // Act
        var response = integrationUtils.post(
                        "/api/auth/login",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    void 요청_본문의_password가_userId에_해당하는_password가_일치하지_않으면_401_Unauthorized_상태코드를_반환한다(
            @Autowired IntegrationTestUtils integrationUtils
    ) {
        // Arrange
        var userId = generateUserId();

        var invalidPassword = generatePassword();
        var request = new AuthRequest(userId, invalidPassword);

        //save member
        integrationUtils.insertDummyMember(userId, generatePassword());

        // Act
        var response = integrationUtils.post(
                        "/api/auth/login",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    void 성공적인_로그인_후_응답에_accessToken과_refreshToken가_포함되어야_한다(
            @Autowired IntegrationTestUtils integrationUtils
    ) {
        // Arrange
        var request = new AuthRequest(generateUserId(), generatePassword());
        integrationUtils.insertDummyMember(request.userId(), request.password());

        // Act
        var response = integrationUtils.post(
                        "/api/auth/login",
                        request)
                .assertSuccess(TokenHolder.class);

        // Assert
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().accessToken()).isNotBlank();
        assertThat(response.getData().refreshToken()).isNotBlank();
    }

    @Test
    void 전달된_토큰은_유효한_JWT_형식이어야_한다(
            @Autowired IntegrationTestUtils integrationUtils
    ) {
        // Arrange
        var userId = generateUserId();
        var password = generatePassword();
        integrationUtils.insertDummyMember(userId, password);

        var request = new AuthRequest(userId, password);

        // Act
        var response = integrationUtils.post(
                        "/api/auth/login",
                        request
                )
                .assertSuccess(TokenHolder.class);

        var accessToken = response.getData().accessToken();
        var refreshToken = response.getData().refreshToken();

        assertJwtFormat(accessToken);
        assertJwtFormat(refreshToken);
    }

    @Test
    void 전달된_토큰은_만료되지_않아야한다(
            @Autowired IntegrationTestUtils integrationUtils,
            @Value("${jwt.token.secret}") String secretKey) {
        // Arrange
        var userId = generateUserId();
        var password = generatePassword();
        integrationUtils.insertDummyMember(userId, password);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var request = new AuthRequest(userId, password);

        // Act
        var response = integrationUtils.post(
                        "/api/auth/login",
                        request
                )
                .assertSuccess(TokenHolder.class);

        var accessToken = response.getData().accessToken();
        var refreshToken = response.getData().refreshToken();

        // Assert
        var accessTokenExpiration = getExpiration(key, accessToken);
        var refreshTokenExpiration = getExpiration(key, refreshToken);

        assertThat(accessTokenExpiration).isAfter(new Date());
        assertThat(refreshTokenExpiration).isAfter(new Date());
    }

    @Test
    void 전달된_토큰에는_사용자의_userId가_포함되어야_한다(
            @Autowired IntegrationTestUtils integrationUtils,
            @Value("${jwt.token.secret}") String secretKey,
            @Autowired MemberQueryRepository memberRepository
    ) {
        // Arrange
        var userId = generateUserId();
        var password = generatePassword();
        integrationUtils.insertDummyMember(userId, password);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var request = new AuthRequest(userId, password);

        // Act
        var response = integrationUtils.post(
                        "/api/auth/login",
                        request
                )
                .assertSuccess(TokenHolder.class);

        // Assert
        var accessToken = response.getData().accessToken();
        var refreshToken = response.getData().refreshToken();

        var accessTokenClaims = getTokenClaims(key, accessToken);
        var refreshTokenClaims = getTokenClaims(key, refreshToken);
        assertThat(accessTokenClaims.getPayload().get("userId")).isNotNull();
        assertThat(refreshTokenClaims.getPayload().get("userId")).isNotNull();

        var currentUserId = accessTokenClaims.getPayload().get("userId").toString();
        var savedMember = memberRepository.findByUserId(currentUserId).orElseThrow();
        assertThat(savedMember).isNotNull();
    }

    private static AuthRequest[] blankUserIdRequests() {
        return new AuthRequest[]{
                new AuthRequest(null, generatePassword()),
                new AuthRequest("", generatePassword()),
                new AuthRequest(" ", generatePassword())
        };
    }

    private static AuthRequest[] blankPasswordRequests() {
        return new AuthRequest[]{
                new AuthRequest("testUser", null),
                new AuthRequest("testUser", ""),
                new AuthRequest("testUser", " ")
        };
    }
}
