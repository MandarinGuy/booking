package org.mandarin.booking.adapter;

import java.util.Collection;
import org.mandarin.booking.MemberAuthority;
import org.mandarin.booking.TokenHolder;

public interface TokenUtils {
    TokenHolder generateToken(String refreshToken);

    TokenHolder generateToken(String userId, String nickName, Collection<MemberAuthority> authorities);

    String getClaim(String token, String claimName);

    Collection<String> getClaims(String token, String claimName);
}
