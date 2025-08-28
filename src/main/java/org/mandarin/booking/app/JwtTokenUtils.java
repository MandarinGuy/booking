package org.mandarin.booking.app;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.mandarin.booking.domain.member.AuthException;
import org.mandarin.booking.domain.member.MemberAuthority;
import org.mandarin.booking.domain.member.TokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils implements TokenUtils {
    private static final String USER_ID = "userId";
    private static final String NICK_NAME = "nickName";
    private static final String ROLES = "roles";

    @Value("${jwt.token.access}")
    private long accessTokenExp;

    @Value("${jwt.token.refresh}")
    private long refreshTokenExp;

    private SecretKey key;

    @Autowired
    public void setKey(@Value("${jwt.token.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenHolder generateToken(String userId, String nickName,
                                     Collection<? extends GrantedAuthority> authorities) {
        String accessToken = generateTokenInternal(userId, nickName, authorities, accessTokenExp);
        String refreshToken = generateTokenInternal(userId, nickName, authorities, refreshTokenExp);
        return new TokenHolder(accessToken, refreshToken);
    }

    @Override
    public TokenHolder generateToken(String refreshToken) {
        var claims = parseClaims(refreshToken);
        String userId = claims.getPayload().get(USER_ID).toString();
        String nickName = claims.getPayload().get(NICK_NAME).toString();
        List<MemberAuthority> authorities = Arrays.stream(claims.getPayload().get(ROLES, String.class).split(","))
                .map(s->s.substring(5))
                .map(MemberAuthority::valueOf)
                .toList();
        return generateToken(userId, nickName, authorities);
    }


    @Override
    public String getClaim(String token, String claimName) {
        Jws<Claims> claims = parseClaims(token);
        return claims.getPayload().get(claimName, String.class);
    }

    @Override
    public Collection<String> getClaims(String token, String claimName) {
        Jws<Claims> claims = parseClaims(token);
        var rawPayload = claims.getPayload().get(claimName, String.class);
        if(rawPayload.isBlank())
            return new ArrayList<>();
        return Arrays.stream(rawPayload.split(",")).toList();
    }

    private String generateTokenInternal(String userId, String nickName,
                                         Collection<? extends GrantedAuthority> authorities, long expiration) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + expiration);
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(userId)
                .claims(Map.of(
                        USER_ID, userId,
                        NICK_NAME, nickName,
                        ROLES, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))
                ))
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    private Jws<Claims> parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (IllegalArgumentException e) {
            throw new AuthException("토큰이 비어있습니다.");
        } catch (JwtException e) {
            throw new AuthException("토큰 검증에 실패했습니다.");
        }
    }
}
