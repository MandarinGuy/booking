package org.mandarin.booking.app.show;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.Show;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class ShowCommandRepository {
    private final ShowRepository jpaRepository;

    public Show insert(Show show) {
        return jpaRepository.save(show);
    }
}

