package org.mandarin.booking.app.hall;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.hall.Hall;
import org.mandarin.booking.domain.hall.HallException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class HallService implements HallValidator, HallFetcher {
    private final HallQueryRepository queryRepository;

    @Override
    public void checkHallExist(Long hallId) {
        if (!queryRepository.existsById(hallId)) {
            throw new HallException("NOT_FOUND", "해당 공연장을 찾을 수 없습니다.");
        }
    }

    @Override
    public Hall fetch(Long hallId) {
        return queryRepository.findById(hallId);
    }
}
