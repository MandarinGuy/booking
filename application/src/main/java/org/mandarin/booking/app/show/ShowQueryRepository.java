package org.mandarin.booking.app.show;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.mandarin.booking.domain.hall.QHall.hall;
import static org.mandarin.booking.domain.show.QShow.show;
import static org.mandarin.booking.domain.show.QShowSchedule.showSchedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.app.NullableQueryFilterBuilder;
import org.mandarin.booking.domain.show.QShowResponse;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;
import org.mandarin.booking.domain.show.ShowException;
import org.mandarin.booking.domain.show.ShowResponse;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ShowQueryRepository {
    private final ShowRepository jpaRepository;
    private final JPAQueryFactory queryFactory;

    boolean existsByName(String title) {
        return jpaRepository.existsByTitle(title);
    }

    Show findById(Long showId) {
        return jpaRepository.findById(showId)
                .orElseThrow(() -> new ShowException("NOT_FOUND", "존재하지 않는 공연입니다."));
    }

    boolean canScheduleOn(Long hallId, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory
                       .selectOne()
                       .from(show)
                       .leftJoin(show.schedules, showSchedule)
                       .where(show.hallId.eq(hallId))
                       .where(showSchedule.startAt.before(endAt),
                               showSchedule.endAt.after(startAt))
                       .fetchFirst() == null;
    }

    SliceView<ShowResponse> fetch(@Nullable Integer page,
                                  @Nullable Integer size,
                                  @Nullable Type type,
                                  @Nullable Rating rating,
                                  @Nullable String q,
                                  @Nullable LocalDate from,
                                  @Nullable LocalDate to) {

        var builder = NullableQueryFilterBuilder.builder()
                .when(type, show.type::eq)
                .when(rating, show.rating::eq)
                .whenHasText(q, show.title::containsIgnoreCase)
                .whenInPeriod(from, to, show.performanceStartDate, show.performanceEndDate)
                .build();

        List<ShowResponse> results = queryFactory
                .select(new QShowResponse(
                        show.id,
                        show.title,
                        show.type,
                        show.rating,
                        show.posterUrl,
                        select(hall.hallName)
                                .from(hall)
                                .where(hall.id.eq(show.hallId)),
                        show.performanceStartDate,
                        show.performanceEndDate))
                .from(show)
                .where(builder)
                .orderBy(show.performanceStartDate.desc(), show.title.asc())
                .offset((long) page * size)
                .limit(size + 1)
                .fetch();

        return new SliceView<>(results, page, size);
    }
}
