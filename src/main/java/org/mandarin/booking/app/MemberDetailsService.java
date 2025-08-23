package org.mandarin.booking.app;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.persist.MemberQueryRepository;
import org.mandarin.booking.domain.member.AuthException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final MemberQueryRepository queryRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var member = queryRepository.findByUserId(username)
                .orElseThrow(() -> new AuthException(" 해당 아이디의 사용자를 찾을 수 없습니다: " + username));
        return User.builder()
                .username(member.getUserId())
                .password("N/A")
                .build();
    }
}
