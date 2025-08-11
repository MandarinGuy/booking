package org.mandarin.booking.persist;

import lombok.RequiredArgsConstructor;
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
}
