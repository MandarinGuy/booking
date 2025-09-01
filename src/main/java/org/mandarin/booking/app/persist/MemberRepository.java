package org.mandarin.booking.app.persist;

import java.util.Optional;
import org.mandarin.booking.domain.member.Member;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, Long> {
    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    Optional<Member> findByUserId(String userId);

    Member save(Member member);
}
