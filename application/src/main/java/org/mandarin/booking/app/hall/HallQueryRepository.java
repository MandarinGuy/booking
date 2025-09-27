package org.mandarin.booking.app.hall;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.hall.Hall;
import org.mandarin.booking.domain.hall.HallException;
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

    public Hall findById(Long hallId) {
        return repository.findById(hallId)
                .orElseThrow(() -> new HallException("NOT_FOUND", "해당 공연장을 찾을 수 없습니다."));
    }
}
