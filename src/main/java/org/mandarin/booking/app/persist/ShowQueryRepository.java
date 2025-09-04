package org.mandarin.booking.app.persist;

import lombok.RequiredArgsConstructor;
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
}
