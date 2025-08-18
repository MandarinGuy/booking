package org.mandarin.booking.app;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.infra.persist.MemberQueryRepository;
import org.mandarin.booking.infra.webapi.dto.TokenHolder;
import org.mandarin.booking.app.port.AuthUseCase;
import org.mandarin.booking.domain.member.AuthException;
import org.mandarin.booking.domain.member.Member;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final SecurePasswordEncoder securePasswordEncoder;
    private final MemberQueryRepository queryRepository;
    private final TokenProvider tokenProvider;

    @Override
    public TokenHolder login(String userId, String password) {
        var member = getMember(userId);
        checkPasswordMatch(member, password);

        return tokenProvider.generateToken(member.getUserId(), member.getNickName());
    }

    @Override
    public TokenHolder reissue(String refreshToken) {
        tokenProvider.validateToken(refreshToken);
        return tokenProvider.generateToken(refreshToken);
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
}
