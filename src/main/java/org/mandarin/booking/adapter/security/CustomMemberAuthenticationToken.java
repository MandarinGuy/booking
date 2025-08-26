package org.mandarin.booking.adapter.security;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomMemberAuthenticationToken extends AbstractAuthenticationToken {
    private final String userId;

    public CustomMemberAuthenticationToken(String userId, GrantedAuthority authority) {
        super(List.of(authority));
        this.userId = userId;
        super.setAuthenticated(true);
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.getDetails();
    }

}
