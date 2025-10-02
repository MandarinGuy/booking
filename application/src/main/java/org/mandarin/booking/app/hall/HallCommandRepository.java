package org.mandarin.booking.app.hall;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.hall.Hall;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class HallCommandRepository {
    private final HallRepository jpaRepository;

    Hall insert(Hall hall) {
        return jpaRepository.save(hall);
    }
}
