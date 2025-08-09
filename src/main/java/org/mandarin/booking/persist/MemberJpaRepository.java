package org.mandarin.booking.persist;

import jakarta.validation.constraints.NotBlank;
import org.mandarin.booking.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);
}
