package org.mandarin.booking.app.persist;

import java.util.Optional;
import org.mandarin.booking.domain.show.Show;
import org.springframework.data.repository.Repository;

interface ShowRepository extends Repository<Show, Long> {
    Show save(Show show);

    boolean existsByTitle(String title);

    Optional<Show> findById(Long showId);
}

