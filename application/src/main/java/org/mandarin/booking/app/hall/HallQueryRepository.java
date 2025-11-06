package org.mandarin.booking.app.hall;

import static org.mandarin.booking.domain.hall.QSeat.seat;
import static org.mandarin.booking.domain.hall.QSection.section;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.hall.Hall;
import org.mandarin.booking.domain.hall.HallException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
class HallQueryRepository {
    private final HallRepository repository;
    private final JPAQueryFactory jpaQueryFactory;

    boolean existsById(Long hallId) {
        return repository.existsById(hallId);
    }

    boolean existsByHallName(String hallName) {
        return repository.existsByHallName(hallName);
    }

    Hall findById(Long hallId) {
        return repository.findById(hallId)
                .orElseThrow(() -> new HallException("NOT_FOUND", "해당 공연장을 찾을 수 없습니다."));
    }

    boolean existsByHallIdAndSectionId(Long hallId, Long sectionId) {
        return findById(hallId).hasSectionOf(sectionId);
    }

    boolean containsSeatIdsBySectionId(Long sectionId, List<Long> seatIds) {
        var fetched = jpaQueryFactory
                .select(seat.id)
                .from(section)
                .join(section.seats, seat)
                .where(section.id.eq(sectionId))
                .fetch();
        return new HashSet<>(fetched).containsAll(seatIds);
    }

    boolean equalsSeatIdsBySectionId(Long sectionId, List<Long> seatIds) {
        var fetched = jpaQueryFactory
                .select(seat.id)
                .from(section)
                .join(section.seats, seat)
                .where(section.id.eq(sectionId))
                .fetch();
        return new HashSet<>(seatIds).equals(new HashSet<>(fetched));
    }
}
