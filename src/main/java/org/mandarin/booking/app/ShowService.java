package org.mandarin.booking.app;

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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowService implements ShowRegisterer {
    private final ShowCommandRepository commandRepository;
    private final ShowQueryRepository queryRepository;

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
        var command = ShowScheduleCreateCommand.from(request);
        var show = queryRepository.findById(command.getShowId());

        show.registerSchedule(command);
        var saved = commandRepository.insert(show);
        return new ShowScheduleRegisterResponse(saved.getId());
    }

    private void checkDuplicateTitle(String title) {
        if (queryRepository.existsByName(title)) {
            throw new ShowException("이미 존재하는 공연 이름입니다:" + title);
        }
    }
}

