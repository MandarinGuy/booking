package org.mandarin.booking.app.persist;

import static org.mandarin.booking.adapter.webapi.ApiStatus.NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.ShowException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShowQueryRepository {
    private final ShowRepository jpaRepository;

    public boolean existsByName(String title) {
        return jpaRepository.existsByTitle(title);
    }

    public Show findById(Long showId) {
        return jpaRepository.findById(showId)
                .orElseThrow(() -> new ShowException(NOT_FOUND, "No show with id=" + showId));
    }
}
