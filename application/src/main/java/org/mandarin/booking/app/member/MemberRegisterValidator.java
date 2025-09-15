package org.mandarin.booking.app.member;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.MemberException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRegisterValidator {
    private final MemberQueryRepository queryRepository;

    void checkDuplicateEmail(String email) {
        if (queryRepository.existsByEmail(email)) {
            throw new MemberException("이미 존재하는 이메일입니다: " + email);
        }
    }

    void checkDuplicateUserId(String userId) {
        if (queryRepository.existsByUserId(userId)) {
            throw new MemberException("이미 존재하는 회원입니다: " + userId);
        }
    }
}
