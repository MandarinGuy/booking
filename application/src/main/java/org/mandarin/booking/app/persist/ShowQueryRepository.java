package org.mandarin.booking.app.persist;


import static org.mandarin.booking.domain.show.QShowSchedule.showSchedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
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
    private final JPAQueryFactory queryFactory;

    public boolean existsByName(String title) {
        return jpaRepository.existsByTitle(title);
    }

    public Show findById(Long showId) {
        return jpaRepository.findById(showId)
                .orElseThrow(() -> new ShowException("NOT_FOUND", "존재하지 않는 공연입니다."));
    }

    public boolean canScheduleOn(Long hallId, LocalDateTime startAt, LocalDateTime endAt) {
        var fetchFirst = queryFactory
                .selectOne()
                .from(showSchedule)
                .where(showSchedule.hallId.eq(hallId))
                .where(showSchedule.startAt.before(endAt))
                .where(showSchedule.endAt.after(startAt))
                .fetchFirst();
        return fetchFirst == null;
    }
}
