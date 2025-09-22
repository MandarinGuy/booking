package org.mandarin.booking.app.member;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.AuthException;
import org.mandarin.booking.TokenHolder;
import org.mandarin.booking.adapter.TokenUtils;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AuthService implements AuthUseCase {
    private final SecurePasswordEncoder securePasswordEncoder;
    private final MemberQueryRepository queryRepository;
    private final TokenUtils tokenUtils;

    @Override
    public TokenHolder login(String userId, String password) {
        var member = getMember(userId);
        checkPasswordMatch(member, password);

        return tokenUtils.generateToken(member.getUserId(), member.getNickName(), member.getAuthorities());
    }

    @Override
    public TokenHolder reissue(String refreshToken) {
        var userId = tokenUtils.getClaim(refreshToken, "userId");
        if (!queryRepository.existsByUserId(userId)) {
            throw new AuthException("회원이 존재하지 않습니다");
        }
        return tokenUtils.generateToken(refreshToken);
    }

    private void checkPasswordMatch(Member member, String password) {
        if (!member.matchesPassword(password, securePasswordEncoder)) {
            throw new AuthException("잘못된 userID 또는 비밀번호");
        }
    }

    private Member getMember(String userId) {
        return queryRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(String.format("회원 아이디 '%s'에 해당하는 회원이 존재하지 않습니다.", userId)));
    }
}
