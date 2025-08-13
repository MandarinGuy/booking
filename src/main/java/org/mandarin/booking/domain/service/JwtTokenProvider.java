package org.mandarin.booking.domain.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {
    private Key key;

    @Autowired
    public void setKey(@Value("${jwt.token.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(String userId, String nickName, long expiration) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + expiration);
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(userId)
                .claims(Map.of(
                        "userId", userId,
                        "nickName", nickName
                ))
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }
}
