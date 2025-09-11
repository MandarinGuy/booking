package org.mandarin.booking.app.show;

import static java.util.Objects.requireNonNull;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.venue.HallExistCheckEvent;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.ShowCreateCommand;
import org.mandarin.booking.domain.show.ShowException;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.mandarin.booking.domain.show.ShowScheduleCreateCommand;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
import org.mandarin.booking.domain.venue.HallException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowService implements ShowRegisterer {
    private final ShowCommandRepository commandRepository;
    private final ShowQueryRepository queryRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ShowRegisterResponse register(ShowRegisterRequest request) {
        var command = ShowCreateCommand.from(request);
        var show = Show.create(command);

        checkDuplicateTitle(show.getTitle());

        var saved = commandRepository.insert(show);
        return new ShowRegisterResponse(requireNonNull(saved.getId()));
    }

    @Override
    public ShowScheduleRegisterResponse registerSchedule(ShowScheduleRegisterRequest request) {
        var show = queryRepository.findById(request.showId());
        var hallId = request.hallId();

        checkHallExist(hallId);
        checkConflictSchedule(hallId, request);

        var command = new ShowScheduleCreateCommand(request.showId(), request.startAt(), request.endAt());

        show.registerSchedule(hallId, command);
        var saved = commandRepository.insert(show);
        return new ShowScheduleRegisterResponse(requireNonNull(saved.getId()));
    }

    private void checkDuplicateTitle(String title) {
        if (queryRepository.existsByName(title)) {
            throw new ShowException("이미 존재하는 공연 이름입니다:" + title);
        }
    }

    private void checkConflictSchedule(Long hallId, ShowScheduleRegisterRequest request) {
        if (!queryRepository.canScheduleOn(hallId, request.startAt(), request.endAt())) {
            throw new ShowException("해당 회차는 이미 공연 스케줄이 등록되어 있습니다.");
        }
    }

    private void checkHallExist(Long hallId) {
        var event = new HallExistCheckEvent(hallId);
        applicationEventPublisher.publishEvent(event);
        if (!event.isExist()) {
            throw new HallException("NOT_FOUND", "해당 공연장을 찾을 수 없습니다.");
        }
    }
}

