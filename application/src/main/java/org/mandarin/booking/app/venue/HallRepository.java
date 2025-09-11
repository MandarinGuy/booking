package org.mandarin.booking.app.venue;

import org.mandarin.booking.domain.venue.Hall;
import org.springframework.data.repository.Repository;

interface HallRepository extends Repository<Hall, Long> {
    Hall save(Hall hall);

    boolean existsById(Long id);
}
