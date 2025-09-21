package org.mandarin.booking.app.show;

import static java.util.Objects.requireNonNull;
import static org.mandarin.booking.domain.EnumUtils.nullableEnum;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.app.venue.HallValidator;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.ShowCreateCommand;
import org.mandarin.booking.domain.show.Show.Type;
import org.mandarin.booking.domain.show.ShowException;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.mandarin.booking.domain.show.ShowResponse;
import org.mandarin.booking.domain.show.ShowScheduleCreateCommand;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowService implements ShowRegisterer, ShowFetcher {
    private final ShowCommandRepository commandRepository;
    private final ShowQueryRepository queryRepository;
    private final HallValidator hallValidator;

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

        hallValidator.checkHallExist(hallId);
        checkConflictSchedule(hallId, request);

        var command = new ShowScheduleCreateCommand(request.showId(), request.startAt(), request.endAt());

        show.registerSchedule(hallId, command);
        var saved = commandRepository.insert(show);
        return new ShowScheduleRegisterResponse(requireNonNull(saved.getId()));
    }

    @Override
    public SliceView<ShowResponse> fetchShows(Integer page, Integer size, String type, String rating, String q,
                                              LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new ShowException("BAD_REQUEST", "from 는 to 보다 과거만 가능합니다.");
        }
        return queryRepository.fetch(page, size,
                nullableEnum(Type.class, type), nullableEnum(Rating.class, rating),
                q.trim(), from, to);
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

}

