package org.mandarin.booking.adapter.security;

import org.mandarin.booking.app.TokenUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@TestConfiguration
@EnableMethodSecurity
@Order(0)
class TestSecurityConfig {

    @Bean
    SecurityFilterChain testOnlyEndpoints(
            HttpSecurity http,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler, TokenUtils tokenUtils) throws Exception {
        http
                .securityMatcher("/test/**")
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/test/without-auth").permitAll()
                        .requestMatchers("/test/with-auth").authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(new JwtFilter(tokenUtils),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
