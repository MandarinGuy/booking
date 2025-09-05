package org.mandarin.booking.adapter.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.FORBIDDEN;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.webapi.ApiStatus.UNAUTHORIZED;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.NoRestDocs;
import org.mandarin.booking.adapter.security.JwtFilterTest.TestAuthController;
import org.mandarin.booking.adapter.security.JwtFilterTest.TestAuthController.TestSecurityConfig;
import org.mandarin.booking.app.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@IntegrationTest
@NoRestDocs
@Import({TestSecurityConfig.class, TestAuthController.class})
class JwtFilterTest {
    private static final String PONG_WITHOUT_AUTH = "pong without auth";
    private static final String PONG_WITH_AUTH = "pong with auth";
    private static final String WITH_USER_ROLE = "pong with user role";


    @Test
    void withoutAuth(@Autowired IntegrationTestUtils testUtils) {
        // Act & Assert
        var response = testUtils.get("/test/without-auth")
                .assertSuccess(String.class);

        assertThat(response.getData()).isEqualTo(PONG_WITHOUT_AUTH);
    }

    @Test
    void withAuth(@Autowired IntegrationTestUtils testUtils) {
        var accessToken = testUtils.getAuthToken();

        // Act & Assert
        var response = testUtils.get(
                        "/test/with-auth"
                )
                .withHeader("Authorization", accessToken)
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
                .withHeader("Authorization", invalidToken)
                .assertFailure();
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(response.getData()).isEqualTo("유효한 토큰이 없습니다.");
    }

    @Test
    void failWithInvalidBearer(@Autowired IntegrationTestUtils testUtils) {
        // Arrange
        var invalidBearer = "Bearer invalid-token";

        // Act
        var response = testUtils.get("/test/with-auth")
                .withHeader("Authorization", invalidBearer)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(response.getData()).isEqualTo("유효한 토큰이 없습니다.");
    }

    @Test
    void lackOfAuthorityMustReturnAccessDenied(@Autowired IntegrationTestUtils testUtils) {
        // Arrange
        var member = testUtils.insertDummyMember("dummy", "dummy", List.of());
        var accessToken = testUtils.getAuthToken(member);

        // Act
        var response = testUtils.get("/test/with-user-role")
                .withHeader("Authorization", accessToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(FORBIDDEN);
        assertThat(response.getData()).isEqualTo("Access Denied");
    }

    @Test
    void blankTokenWillFailToAuth(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var accessToken = "Bearer ";

        // Act
        var response = testUtils.get("/test/with-auth")
                .withHeader("Authorization", accessToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(response.getData()).isEqualTo("토큰이 비어있습니다.");
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

        @GetMapping("/with-user-role")
        public String pingWithUserRole() {
            return WITH_USER_ROLE;
        }

        @TestConfiguration
        @EnableMethodSecurity
        static class TestSecurityConfig {

            @Bean
            @Order(0)
            SecurityFilterChain testOnlyEndpoints(
                    HttpSecurity http,
                    AuthenticationEntryPoint authenticationEntryPoint,
                    AccessDeniedHandler accessDeniedHandler, TokenUtils tokenUtils,
                    AuthenticationProvider authenticationProvider) throws Exception {
                http
                        .securityMatcher("/test/**")
                        .authorizeHttpRequests(a -> a
                                .requestMatchers("/test/without-auth").permitAll()
                                .requestMatchers("/test/with-auth").authenticated()
                                .requestMatchers("/test/with-user-role").hasAuthority("USER")
                        )
                        .formLogin(AbstractHttpConfigurer::disable)
                        .csrf(AbstractHttpConfigurer::disable)
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .exceptionHandling(ex -> ex
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler))
                        .addFilterBefore(new JwtFilter(tokenUtils, authenticationProvider::authenticate),
                                UsernamePasswordAuthenticationFilter.class);
                return http.build();
            }
        }

    }
}
