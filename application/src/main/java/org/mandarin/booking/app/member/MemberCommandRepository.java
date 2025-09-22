package org.mandarin.booking.app.member;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
class MemberCommandRepository {
    private final MemberRepository jpaRepository;

    Member insert(Member member) {
        return jpaRepository.save(member);
    }
}
