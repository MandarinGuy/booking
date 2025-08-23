package org.mandarin.booking.adapter.security;

import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.adapter.security.JwtFilterTest.TestAuthController;
import org.mandarin.booking.adapter.security.JwtFilterTest.TestSecurityConfig;
import org.mandarin.booking.app.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestSecurityConfig.class, TestAuthController.class})
class JwtFilterTest {
    private static final String PONG_WITHOUT_AUTH = "pong without auth";
    private static final String PONG_WITH_AUTH = "pong with auth";

    @Autowired
    MockMvc mockMvc;


    @Test
    void withoutAuth() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/test/without-auth"))
                .andExpect(status().isOk())
                .andExpect(content().string(PONG_WITHOUT_AUTH))
                .andDo(print());
    }

    @Test
    void withAuth(@Autowired TokenUtils tokenUtils) throws Exception {
        var userId = generateUserId();
        var nickName = generateNickName();
        var accessToken = tokenUtils.generateToken(userId, nickName).accessToken();

        mockMvc.perform(get("/test/without-auth")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string(PONG_WITHOUT_AUTH))
                .andDo(print());
    }

    @Test
    void failToAuth() throws Exception {
        // Arrange
        var invalidToken = "invalid token";

        // Act & Assert
        mockMvc.perform(get("/test/with-auth")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{status: \"UNAUTHORIZED\"}"))
                .andDo(print());
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

    @TestConfiguration
    static
    class TestSecurityConfig {
        @Autowired
        ObjectMapper objectMapper;

        @Bean(name = "testSecurityFilterChain")
        @Order(1)
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http,
                                                    TokenUtils tokenUtils,
                                                    AuthenticationProvider preAuthProvider) throws Exception {
            AuthenticationManager authManager = preAuthProvider::authenticate;
            return http
                    .securityMatcher("/test/**")
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/test/without-auth").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .csrf(AbstractHttpConfigurer::disable)
                    .authenticationProvider(preAuthProvider)
                    .authenticationManager(authManager)
                    .addFilterBefore(new JwtFilter(tokenUtils, objectMapper), UsernamePasswordAuthenticationFilter.class)
                    .build();
        }
    }
}
