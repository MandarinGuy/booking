package org.mandarin.booking.webapi.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.adapter.webapi.dto.AuthRequest;
import org.mandarin.booking.adapter.webapi.dto.TokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;

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
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.auth.login.POST_specs#blankUserIdRequests")
    void 요청_본문의_userId가_누락된_경우_400_Bad_Request_상태코드를_반환한다(
            // Arrange
            AuthRequest request,
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.auth.login.POST_specs#blankPasswordRequests")
    void 요청_본문의_password가_누락된_경우_400_Bad_Request_상태코드를_반환한다(
            // Arrange
            AuthRequest request,
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 존재하지_않는_userId_비밀번호로_요청하면_401_Unauthorized_상태코드를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        var request = new AuthRequest("nonExistentUser", generatePassword());

        // don't need to save member in the database

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
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
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
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
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getBody().accessToken()).isNotBlank();
        assertThat(response.getBody().refreshToken()).isNotBlank();

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
                request,
                TokenHolder.class
        );

        var accessToken = response.getBody().accessToken();
        var refreshToken = response.getBody().refreshToken();

        assertThat(accessToken.split("\\.")).hasSize(3);
        assertThat(refreshToken.split("\\.")).hasSize(3);

        assertThat(accessToken).matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");
        assertThat(refreshToken).matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");

        assertThatCode(() -> {
            String[] accessTokenParts = accessToken.split("\\.");
            String[] refreshTokenParts = refreshToken.split("\\.");

            java.util.Base64.getUrlDecoder().decode(accessTokenParts[0]);
            java.util.Base64.getUrlDecoder().decode(accessTokenParts[1]);
            java.util.Base64.getUrlDecoder().decode(refreshTokenParts[0]);
            java.util.Base64.getUrlDecoder().decode(refreshTokenParts[1]);
        }).doesNotThrowAnyException();
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
                request,
                TokenHolder.class
        );

        var accessToken = response.getBody().accessToken();
        var refreshToken = response.getBody().refreshToken();

        // Assert
        var accessTokenExpiration = getExpiration(key, accessToken);
        var refreshTokenExpiration = getExpiration(key, refreshToken);

        assertThat(accessTokenExpiration).isAfter(new Date());
        assertThat(refreshTokenExpiration).isAfter(new Date());
    }
    
    @Test
    void 전달된_토큰에는_사용자의_userId가_포함되어야_한다(
        @Autowired IntegrationTestUtils  integrationUtils,
        @Value("${jwt.token.secret}") String secretKey
    ){
        // Arrange
        var userId = generateUserId();
        var password = generatePassword();
        integrationUtils.insertDummyMember(userId, password);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var request = new AuthRequest(userId, password);

        // Act
        var response = integrationUtils.post(
                "/api/auth/login",
                request,
                TokenHolder.class
        );


        // Assert
        var accessToken = response.getBody().accessToken();
        var refreshToken = response.getBody().refreshToken();

        var accessTokenClaims = getTokenClaims(key, accessToken);
        var refreshTokenClaims = getTokenClaims(key, refreshToken);
        assertThat(accessTokenClaims.getPayload().get("userId")).isNotNull();
        assertThat(refreshTokenClaims.getPayload().get("userId")).isNotNull();
    }

    private static Jws<Claims> getTokenClaims(SecretKey key, String accessToken) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken);
    }

    private static Date getExpiration(SecretKey key, String token) {
        return getTokenClaims(key, token)
                .getPayload()
                .getExpiration();
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
