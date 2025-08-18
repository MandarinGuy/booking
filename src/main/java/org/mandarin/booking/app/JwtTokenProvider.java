package org.mandarin.booking.app;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.mandarin.booking.infra.webapi.dto.TokenHolder;
import org.mandarin.booking.domain.member.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {
    private static final String USER_ID = "userId";
    private static final String NICK_NAME = "nickName";

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
    public TokenHolder generateToken(String refreshToken) {
        var claims = parseClaims(refreshToken);
        String userId = claims.getPayload().get(USER_ID).toString();
        String nickName = claims.getPayload().get(NICK_NAME).toString();
        return generateToken(userId, nickName);
    }

    @Override
    public TokenHolder generateToken(String userId, String nickName) {
        String accessToken = generateTokenInternal(userId, nickName, accessTokenExp);
        String refreshToken = generateTokenInternal(userId, nickName, refreshTokenExp);
        return new TokenHolder(accessToken, refreshToken);
    }

    private String generateTokenInternal(String userId, String nickName, long expiration) {
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
                        NICK_NAME, nickName
                ))
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    private Jws<Claims> parseClaims(String refreshToken) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken);
    }

    @Override
    public void validateToken(String token) {
        try {
            parseClaims(token);
        } catch (IllegalArgumentException e) {
            throw new AuthException("올바르지 않은 토큰입니다.");
        } catch (JwtException e) {
            throw new AuthException("토큰 검증에 실패했습니다.");
        }
    }
}
