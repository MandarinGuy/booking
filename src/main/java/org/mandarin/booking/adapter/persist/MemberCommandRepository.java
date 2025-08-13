package org.mandarin.booking.adapter.persist;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.model.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class MemberCommandRepository {
    private final MemberJpaRepository jpaRepository;

    public Member insert(Member member) {
        return jpaRepository.save(member);
    }
}
