package org.mandarin.booking.app.hall;

import java.util.Optional;
import org.mandarin.booking.domain.hall.Hall;
import org.springframework.data.repository.Repository;

interface HallRepository extends Repository<Hall, Long> {
    boolean existsById(Long id);

    Optional<Hall> findById(Long id);
}
