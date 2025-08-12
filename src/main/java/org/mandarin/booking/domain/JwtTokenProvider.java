package org.mandarin.booking.domain;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.token.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Override
    public String generateToken(String userId, String nickName, long expiration) {
        return Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setSubject(userId)
                .claims(Map.of("userId", userId, "nickName", nickName))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .issuedAt(new Date())
                .compact();
    }
}
