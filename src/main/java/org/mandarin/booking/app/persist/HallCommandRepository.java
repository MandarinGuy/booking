package org.mandarin.booking.app.persist;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.venue.Hall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class HallCommandRepository {
    private final HallRepository repository;

    public Hall insert(Hall hall) {
        return repository.save(hall);
    }
}

