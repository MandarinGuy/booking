package org.mandarin.booking.app.hall;

import java.util.List;
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
    public Hall fetch(Long hallId) {
        return queryRepository.findById(hallId);
    }

    @Override
    public void checkHallExistByHallId(Long hallId) {
        if (!queryRepository.existsById(hallId)) {
            throw new HallException("NOT_FOUND", "해당 공연장을 찾을 수 없습니다.");
        }
    }

    @Override
    public void checkHallExistByHallName(String hallName) {
        if (queryRepository.existsByHallName(hallName)) {
            throw new HallException("INTERNAL_SERVER_ERROR", "이미 존재하는 공연장 이름입니다.");
        }
    }

    @Override
    public void checkHallExistBySectionId(Long hallId, Long sectionId) {
        if (!queryRepository.existsByHallIdAndSectionId(hallId, sectionId)) {
            throw new HallException("NOT_FOUND", "해당 공연장에 섹션이 존재하지 않습니다.");
        }
    }

    @Override
    public void checkHallInvalidSeatIds(Long sectionId, List<Long> seatIds) {
        if (!queryRepository.containsSeatIdsBySectionId(sectionId, seatIds)) {
            throw new HallException("BAD_REQUEST", "해당 섹션에 존재하지 않는 좌석이 있습니다.");
        }
    }

    @Override
    public HallRegisterResponse register(String userId, HallRegisterRequest request) {
        checkHallExistByHallName(request.hallName());

        var hall = Hall.create(request.hallName(), request.sections(), userId);

        var saved = commandRepository.insert(hall);

        return new HallRegisterResponse(saved.getId());
    }
}
