package org.mandarin.booking.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum MemberAuthority implements GrantedAuthority {
    USER("USER"),
    DISTRIBUTOR("DISTRIBUTOR"),
    ADMIN("ADMIN");
    private final String authority;
}
