package org.mandarin.booking.app.persist;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.ShowException;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.venue.Hall;
import org.mandarin.booking.domain.venue.HallException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HallQueryRepository {
    private final HallRepository jpaRepository;

    public Hall getScreenableHall(ShowScheduleRegisterRequest request) {
        var hall = jpaRepository.findById(request.hallId())
                .orElseThrow(() -> new HallException("NOT_FOUND", "해당 공연장을 찾을 수 없습니다."));
        if (!hall.canScheduleOn(request.startAt(), request.endAt())) {
            throw new ShowException("해당 회차는 이미 공연 스케줄이 등록되어 있습니다.");
        }
        return hall;
    }
}
