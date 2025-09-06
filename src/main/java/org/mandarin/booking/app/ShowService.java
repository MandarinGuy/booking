package org.mandarin.booking.app;

import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.webapi.ApiStatus.NOT_FOUND;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.persist.ShowCommandRepository;
import org.mandarin.booking.app.persist.ShowQueryRepository;
import org.mandarin.booking.app.port.ShowRegisterer;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.ShowCreateCommand;
import org.mandarin.booking.domain.show.ShowException;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.mandarin.booking.domain.show.ShowSchedule.ShowScheduleCreateCommand;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
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
        return new ShowRegisterResponse(saved.getId());
    }

    @Override
    public ShowScheduleRegisterResponse registerSchedule(ShowScheduleRegisterRequest request) {
        checkHallId(request);
        var show = queryRepository.findById(request.showId());
        checkShowScheduleRange(show, request.startAt(), request.endAt());

        var command = ShowScheduleCreateCommand.from(request);

        show.registerSchedule(command);
        var saved = commandRepository.insert(show);
        return new ShowScheduleRegisterResponse(saved.getId());
    }

    private void checkHallId(ShowScheduleRegisterRequest request) {
        var event = new HallVerificationEvent(request.hallId());

        applicationEventPublisher.publishEvent(event);
        if (!event.isVerified()) {
            throw new ShowException(NOT_FOUND, "존재하지 않는 공연장입니다: " + request.hallId());
        }
    }

    private void checkDuplicateTitle(String title) {
        if (queryRepository.existsByName(title)) {
            throw new ShowException("이미 존재하는 공연 이름입니다:" + title);
        }
    }

    private static void checkShowScheduleRange(Show show, LocalDateTime scheduleStartAt, LocalDateTime scheduleEndAt) {
        if (!show.isInSchedule(scheduleStartAt, scheduleEndAt)) {
            throw new ShowException(BAD_REQUEST, "공연 기간 범위를 벗어나는 일정입니다.");
        }
    }
}

