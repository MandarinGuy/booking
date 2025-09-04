package org.mandarin.booking.app.persist;

import org.mandarin.booking.domain.show.Show;
import org.springframework.data.repository.Repository;

public interface ShowRepository extends Repository<Show, Long> {
    Show save(Show show);
}

