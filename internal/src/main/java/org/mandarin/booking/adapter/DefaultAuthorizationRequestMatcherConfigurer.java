package org.mandarin.booking.adapter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

class DefaultAuthorizationRequestMatcherConfigurer implements AuthorizationRequestMatcherConfigurer {
    @Override
    public void authorizeRequests(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth.anyRequest().authenticated();
    }
}
