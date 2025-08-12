package org.mandarin.booking.domain;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.adapter.webapi.AuthRequest;
import org.mandarin.booking.adapter.webapi.AuthUseCase;
import org.mandarin.booking.adapter.webapi.TokenHolder;
import org.mandarin.booking.persist.MemberQueryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final PasswordEncoder passwordEncoder;
    private final MemberQueryRepository queryRepository;
    private final TokenProvider tokenProvider;

    @Value("${jwt.token.access}")
    private long accessTokenExpiration;

    @Value("${jwt.token.refresh}")
    private long refreshTokenExpiration;

    @Override
    public TokenHolder login(AuthRequest request) {
        var member = queryRepository.findByUserId(request.userId());
        if (!member.matchesPassword(request.password(), passwordEncoder)) {
            throw new IllegalArgumentException("Invalid userId or password");
        }

        return generateTokens(member);
    }

    private TokenHolder generateTokens(Member member) {
        var accessToken =
                tokenProvider.generateToken(member.getUserId(), member.getNickName(), accessTokenExpiration);
        var refreshToken =
                tokenProvider.generateToken(member.getUserId(), member.getNickName(), refreshTokenExpiration);
        return new TokenHolder(accessToken, refreshToken);
    }
}
