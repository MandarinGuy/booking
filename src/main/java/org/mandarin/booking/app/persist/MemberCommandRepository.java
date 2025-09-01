package org.mandarin.booking.app.persist;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class MemberCommandRepository {
    private final MemberRepository jpaRepository;

    public Member insert(Member member) {
        return jpaRepository.save(member);
    }
}
