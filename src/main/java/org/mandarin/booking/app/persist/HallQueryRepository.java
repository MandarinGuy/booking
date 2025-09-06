package org.mandarin.booking.app.persist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HallQueryRepository {
    private final HallRepository jpaRepository;

    public boolean existsById(Long hallId) {
        return jpaRepository.existsById(hallId);
    }
}
