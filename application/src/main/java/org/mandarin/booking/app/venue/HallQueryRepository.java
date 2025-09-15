package org.mandarin.booking.app.venue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HallQueryRepository {
    private final HallRepository repository;

    public boolean existsById(Long hallId) {
        return repository.existsById(hallId);
    }
}
