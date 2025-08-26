package org.mandarin.booking.adapter.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomMemberAuthenticationToken extends AbstractAuthenticationToken {
    private final String userId;

    public CustomMemberAuthenticationToken(String userId) {
        super(List.of());
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

    public void addAuthorities(Collection<? extends GrantedAuthority> authorities) {
        super.getAuthorities().addAll(authorities);
    }
}
