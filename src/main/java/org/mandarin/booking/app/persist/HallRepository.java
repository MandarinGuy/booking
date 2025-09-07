package org.mandarin.booking.app.persist;

import java.util.Optional;
import org.mandarin.booking.domain.venue.Hall;
import org.springframework.data.repository.Repository;

public interface HallRepository extends Repository<Hall, Long> {
    boolean existsById(Long id);

    Hall save(Hall hall);

    Optional<Hall> findById(Long hallId);
}
