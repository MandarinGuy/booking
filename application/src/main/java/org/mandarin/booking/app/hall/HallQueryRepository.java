package org.mandarin.booking.app.hall;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
class HallQueryRepository {
    private final HallRepository repository;

    boolean existsById(Long hallId) {
        return repository.existsById(hallId);
    }
}
