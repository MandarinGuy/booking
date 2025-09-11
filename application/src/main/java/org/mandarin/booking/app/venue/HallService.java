package org.mandarin.booking.app.venue;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.venue.HallException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class HallService implements HallValidator {
    private final HallQueryRepository queryRepository;

    @Override
    public void checkHallExist(Long hallId) {
        if (!queryRepository.existsById(hallId)) {
            throw new HallException("NOT_FOUND", "해당 공연장을 찾을 수 없습니다.");
        }
    }
}
