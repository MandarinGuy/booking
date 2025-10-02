package org.mandarin.booking.app.hall;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.hall.Hall;
import org.mandarin.booking.domain.hall.HallException;
import org.mandarin.booking.domain.hall.HallRegisterRequest;
import org.mandarin.booking.domain.hall.HallRegisterResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class HallService implements HallValidator, HallFetcher, HallRegisterer {
    private final HallQueryRepository queryRepository;
    private final HallCommandRepository commandRepository;

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

    @Override
    public HallRegisterResponse register(String userId, HallRegisterRequest request) {
        if (queryRepository.existsByHallName(request.hallName())) {
            throw new HallException("INTERNAL_SERVER_ERROR", "이미 존재하는 공연장 이름입니다.");
        }

        var hall = Hall.create(request.hallName(), userId);

        var saved = commandRepository.insert(hall);

        return new HallRegisterResponse(saved.getId());
    }
}
