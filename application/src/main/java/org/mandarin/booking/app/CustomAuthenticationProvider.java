package org.mandarin.booking.app;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.AuthException;
import org.mandarin.booking.adapter.CustomMemberAuthenticationToken;
import org.mandarin.booking.app.persist.MemberQueryRepository;
import org.mandarin.booking.domain.member.Member;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final MemberQueryRepository queryRepository;

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomMemberAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof CustomMemberAuthenticationToken token) {
            var userId = token.getName();
            var member = queryRepository.findByUserId(userId)
                    .orElseThrow(() -> new AuthException("해당 아이디의 사용자를 찾을 수 없습니다: " + userId));

            specifyToken(token, member);
            return token;
        }
        throw new AuthException("Unsupported authentication type: " + authentication.getClass());
    }

    private void specifyToken(CustomMemberAuthenticationToken token, Member member) {
        var details = User.builder()
                .username(member.getUserId())
                .authorities(member.getParsedAuthorities())
                .password(member.getPasswordHash())
                .build();
        token.setDetails(details);// set user details
        token.setAuthenticated(true);
    }
}
