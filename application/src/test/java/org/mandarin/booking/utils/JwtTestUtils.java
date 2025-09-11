package org.mandarin.booking.utils;

import static java.util.Base64.getUrlDecoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtTestUtils {
    public static Jws<Claims> getTokenClaims(SecretKey key, String accessToken) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken);
    }

    public static Date getExpiration(SecretKey key, String token) {
        return getTokenClaims(key, token)
                .getPayload()
                .getExpiration();
    }

    public static void assertJwtFormat(String accessToken) {
        assertThat(accessToken.split("\\.")).hasSize(3);
        assertThat(accessToken).matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");

        assertThatCode(() -> {
            String[] accessTokenParts = accessToken.split("\\.");
            getUrlDecoder().decode(accessTokenParts[0]);
            getUrlDecoder().decode(accessTokenParts[1]);
        }).doesNotThrowAnyException();
    }
}
