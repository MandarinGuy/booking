package org.mandarin.booking.app.persist;

import java.util.Optional;
import org.mandarin.booking.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    Optional<Member> findByUserId(String userId);
}
