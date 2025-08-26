package org.mandarin.booking.adapter.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.webapi.ApiStatus.UNAUTHORIZED;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.adapter.security.JwtFilterTest.TestAuthController;
import org.mandarin.booking.app.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@IntegrationTest
@Import({TestSecurityConfig.class, TestAuthController.class})
class JwtFilterTest {
    private static final String PONG_WITHOUT_AUTH = "pong without auth";
    private static final String PONG_WITH_AUTH = "pong with auth";


    @Test
    void withoutAuth(@Autowired IntegrationTestUtils testUtils) {
        // Act & Assert
        var response = testUtils.get("/test/without-auth")
                .assertSuccess(String.class);

        assertThat(response.getData()).isEqualTo(PONG_WITHOUT_AUTH);
    }

    @Test
    void withAuth(@Autowired IntegrationTestUtils testUtils,
                  @Autowired TokenUtils tokenUtils) {
        var userId = generateUserId();
        var nickName = generateNickName();
        var accessToken = tokenUtils.generateToken(userId, nickName).accessToken();
        var password = generatePassword();
        testUtils.insertDummyMember(userId, password);

        // Act & Assert
        var response = testUtils.get(
                        "/test/with-auth"
                )
                .withHeader("Authorization", "Bearer " + accessToken)
                .assertSuccess(String.class);
//
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
        assertThat(response.getData()).isEqualTo(PONG_WITH_AUTH);
    }

    @Test
    void failToAuth(@Autowired IntegrationTestUtils testUtils) {
        // Arrange
        var invalidToken = "invalid token";

        // Act & Assert
        var response = testUtils.get("/test/with-auth")
                .withHeader("Authorization", "Bearer " + invalidToken)
                .assertFailure();
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(response.getData()).isEqualTo("토큰 검증에 실패했습니다.");
    }

    @RestController
    @RequestMapping("/test")
    static class TestAuthController {
        @GetMapping("/without-auth")
        public String ping() {
            return PONG_WITHOUT_AUTH;
        }

        @GetMapping("/with-auth")
        public String pingWithAuth() {
            return PONG_WITH_AUTH;
        }
    }
}
