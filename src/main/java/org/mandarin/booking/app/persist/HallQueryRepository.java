package org.mandarin.booking.app.persist;

import lombok.RequiredArgsConstructor;
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

    public Hall getHall(ShowScheduleRegisterRequest request) {
        return jpaRepository.findById(request.hallId())
                .orElseThrow(() -> new HallException("NOT_FOUND", "해당 공연장을 찾을 수 없습니다."));
    }
}
