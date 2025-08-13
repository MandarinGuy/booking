package org.mandarin.booking.domain.service;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.adapter.webapi.AuthRequest;
import org.mandarin.booking.app.port.AuthUseCase;
import org.mandarin.booking.adapter.webapi.TokenHolder;
import org.mandarin.booking.domain.error.AuthException;
import org.mandarin.booking.adapter.persist.MemberQueryRepository;
import org.mandarin.booking.domain.model.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final SecurePasswordEncoder securePasswordEncoder;
    private final MemberQueryRepository queryRepository;
    private final TokenProvider tokenProvider;

    @Value("${jwt.token.access}")
    private long accessTokenExp;

    @Value("${jwt.token.refresh}")
    private long refreshTokenExp;

    @Override
    public TokenHolder login(AuthRequest request) {
        var member = getMember(request.userId());
        checkPasswordMatch(member, request.password());

        return generateTokens(member);
    }

    private void checkPasswordMatch(Member member, String password) {
        if (!member.matchesPassword(password, securePasswordEncoder)) {
            throw new AuthException("Invalid userId or password");
        }
    }

    private Member getMember(String userId) {
        return queryRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(String.format("회원 아이디 '%s'에 해당하는 회원이 존재하지 않습니다.", userId)));
    }

    private TokenHolder generateTokens(Member member) {
        var accessToken = tokenProvider.generateToken(member.getUserId(), member.getNickName(), accessTokenExp);
        var refreshToken = tokenProvider.generateToken(member.getUserId(), member.getNickName(), refreshTokenExp);
        return new TokenHolder(accessToken, refreshToken);
    }
}
