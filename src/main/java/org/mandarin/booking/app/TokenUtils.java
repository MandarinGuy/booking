package org.mandarin.booking.app;

import java.util.Collection;
import org.mandarin.booking.domain.member.TokenHolder;
import org.springframework.security.core.GrantedAuthority;

public interface TokenUtils {
    TokenHolder generateToken(String refreshToken);

    TokenHolder generateToken(String userId, String nickName, Collection<? extends GrantedAuthority> authorities);

    String getClaim(String token, String claimName);

    Collection<String> getClaims(String token, String claimName);
}
