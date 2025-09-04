package org.mandarin.booking.app.persist;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final MemberRepository jpaRepository;

    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    public boolean existsByUserId(String userId) {
        return jpaRepository.existsByUserId(userId);
    }

    public Optional<Member> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId);
    }
}
