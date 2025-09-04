package org.mandarin.booking.domain.member;

import org.springframework.security.core.GrantedAuthority;

public enum MemberAuthority implements GrantedAuthority {
    USER,
    DISTRIBUTOR,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name().toUpperCase();
    }
}
