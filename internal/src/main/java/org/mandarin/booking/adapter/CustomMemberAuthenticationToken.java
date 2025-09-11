package org.mandarin.booking.adapter;

import java.util.Collection;
import org.jspecify.annotations.NullUnmarked;
import org.mandarin.booking.MemberAuthority;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomMemberAuthenticationToken extends AbstractAuthenticationToken {
    private final String userId;

    public CustomMemberAuthenticationToken(String userId, Collection<MemberAuthority> authorities) {
        super(authorities.stream()
                .map(authority -> (GrantedAuthority) authority::getAuthority)
                .toList());
        this.userId = userId;
        super.setAuthenticated(true);
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    @NullUnmarked
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.getDetails();
    }

}
