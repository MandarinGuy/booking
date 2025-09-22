package org.mandarin.booking.app.member;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
class MemberQueryRepository {
    private final MemberRepository jpaRepository;

    Optional<Member> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId);
    }

    boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    boolean existsByUserId(String userId) {
        return jpaRepository.existsByUserId(userId);
    }
}
