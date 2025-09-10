package org.mandarin.booking.app;

import java.util.Collection;
import org.mandarin.booking.domain.member.MemberAuthority;
import org.mandarin.booking.domain.member.TokenHolder;

public interface TokenUtils {
    TokenHolder generateToken(String refreshToken);

    TokenHolder generateToken(String userId, String nickName, Collection<MemberAuthority> authorities);

    String getClaim(String token, String claimName);

    Collection<String> getClaims(String token, String claimName);
}
