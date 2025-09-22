package org.mandarin.booking.app.hall;

import org.mandarin.booking.domain.hall.Hall;
import org.springframework.data.repository.Repository;

interface HallRepository extends Repository<Hall, Long> {
    Hall save(Hall hall);

    boolean existsById(Long id);
}
