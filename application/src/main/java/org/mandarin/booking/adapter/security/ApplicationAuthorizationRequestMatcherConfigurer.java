package org.mandarin.booking.adapter.security;

import org.mandarin.booking.adapter.AuthorizationRequestMatcherConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
class ApplicationAuthorizationRequestMatcherConfigurer implements AuthorizationRequestMatcherConfigurer {
    @Override
    public void authorizeRequests(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                .requestMatchers(HttpMethod.POST, "/api/member").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/reissue").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/show/schedule").hasAuthority("ROLE_DISTRIBUTOR")
                .requestMatchers(HttpMethod.GET, "/api/show").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/show/*").permitAll()// 인증이 필요한 GET /show/* 엔드포인트 추가시 설정을 이 줄 아래에
                .requestMatchers(HttpMethod.POST, "/api/show").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/hall").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated();
    }
}
