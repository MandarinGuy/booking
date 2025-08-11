package org.mandarin.booking.persist;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final MemberJpaRepository jpaRepository;

    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    public boolean existsByUserId(String userId) {
        return jpaRepository.existsByUserId(userId);
    }

    public Member findByUserId(String userId) {
        return jpaRepository.findByUserId(userId)
                .orElseThrow(()-> new IllegalArgumentException(String.format("회원 아이디 '%s'에 해당하는 회원이 존재하지 않습니다.", userId)));
    }
}
