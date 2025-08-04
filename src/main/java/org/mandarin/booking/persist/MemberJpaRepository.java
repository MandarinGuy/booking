package org.mandarin.booking.persist;

import org.mandarin.booking.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    boolean existsByUserId(String userId);
}
