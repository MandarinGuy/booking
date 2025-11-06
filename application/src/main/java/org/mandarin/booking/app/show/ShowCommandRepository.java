package org.mandarin.booking.app.show;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.Show;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
class ShowCommandRepository {
    private final EntityManager entityManager;

    Show insert(Show show) {
        entityManager.persist(show);
        entityManager.flush();
        entityManager.refresh(show);
        return show;
    }
}

