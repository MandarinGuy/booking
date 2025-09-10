package org.mandarin.booking.app.persist;

import org.mandarin.booking.domain.venue.Hall;
import org.springframework.data.repository.Repository;

public interface HallRepository extends Repository<Hall, Long> {
    Hall save(Hall hall);

    boolean existsById(Long id);
}
