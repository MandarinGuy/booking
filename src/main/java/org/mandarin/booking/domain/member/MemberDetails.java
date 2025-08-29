package org.mandarin.booking.domain.member;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class MemberDetails implements UserDetails {
    private final String userId;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private MemberDetails(String userId, String password, Collection<MemberAuthority> authorities) {
        this.userId = userId;
        this.password = password;
        this.authorities = authorities;
    }

    public static MemberDetails from(Member member) {
        String userId = member.getUserId();
        String password = member.getPasswordHash();
        Collection<MemberAuthority> authorities = member.getAuthorities();
        return new MemberDetails(userId, password, authorities);
    }

    @Override
    public String getUsername() {
        return userId;
    }
}
