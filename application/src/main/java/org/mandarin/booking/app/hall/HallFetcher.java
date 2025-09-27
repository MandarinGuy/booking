package org.mandarin.booking.app.hall;

import org.mandarin.booking.domain.hall.Hall;

public interface HallFetcher {
    Hall fetch(Long hallId);
}
