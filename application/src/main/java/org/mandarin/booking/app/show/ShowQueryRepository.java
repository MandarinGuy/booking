package org.mandarin.booking.app.show;


import static org.mandarin.booking.domain.show.QShow.show;
import static org.mandarin.booking.domain.show.QShowSchedule.showSchedule;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.mandarin.booking.adapter.SliceView;
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

    public SliceView<ShowResponse> fetch(@Nullable Integer page,
                                         @Nullable Integer size,
                                         @Nullable Type type,
                                         @Nullable Rating rating,
                                         @Nullable String q,
                                         @Nullable LocalDate from,
                                         @Nullable LocalDate to) {
        BooleanBuilder builder = new BooleanBuilder();
        if (type != null) {
            builder.and(show.type.eq(type));
        }
        if (rating != null) {
            builder.and(show.rating.eq(rating));
        }
        if (q != null) {
            builder.and(show.title.like(q));
        }
        if (from != null && to != null) {
            builder.and(show.performanceStartDate.after(from))
                    .and(show.performanceEndDate.before(to));
        } else if (from != null) {
            builder.and(show.performanceStartDate.after(from));
        } else if (to != null) {
            builder.and(show.performanceEndDate.before(to));
        }

        List<ShowResponse> results = queryFactory
                .select(new QShowResponse(
                        show.id,
                        show.title,
                        show.type,
                        show.rating,
                        show.posterUrl,
                        Expressions.nullExpression(), // venueName
                        show.performanceStartDate,
                        show.performanceEndDate))
                .from(show)
                .where(builder)
                .orderBy(show.performanceStartDate.desc(), show.title.asc())
                .offset((long) page * size)
                .limit(size + 1)
                .fetch();

        boolean hasNext = results.size() > size;

        return new SliceView<>(results, page, size, hasNext);
    }
}
