package org.mandarin.booking.app.show;

import static java.util.Objects.requireNonNull;
import static org.mandarin.booking.domain.EnumUtils.nullableEnum;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.app.hall.HallFetcher;
import org.mandarin.booking.app.hall.HallValidator;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.ShowCreateCommand;
import org.mandarin.booking.domain.show.Show.Type;
import org.mandarin.booking.domain.show.ShowDetailResponse;
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
class ShowService implements ShowRegisterer, ShowFetcher {
    private final ShowCommandRepository commandRepository;
    private final ShowQueryRepository queryRepository;
    private final HallValidator hallValidator;
    private final HallFetcher hallFetcher;

    @Override
    public ShowRegisterResponse register(ShowRegisterRequest request) {
        var hallId = request.hallId();

        hallValidator.checkHallExistByHallId(hallId);
        var command = ShowCreateCommand.from(request);
        var show = Show.create(hallId, command);

        checkDuplicateTitle(show.getTitle());

        var saved = commandRepository.insert(show);
        return new ShowRegisterResponse(requireNonNull(saved.getId()));
    }

    @Override
    public ShowScheduleRegisterResponse registerSchedule(ShowScheduleRegisterRequest request) {
        var show = queryRepository.findById(request.showId());
        hallValidator.checkHallExistBySectionId(show.getHallId(), request.use().sectionId());
        hallValidator.checkHallInvalidSeatIds(request.use().excludeSeatIds(), request.use().sectionId());

        checkConflictSchedule(show.getHallId(), request);
        var command = new ShowScheduleCreateCommand(request.showId(), request.startAt(), request.endAt());

        show.registerSchedule(command);
        var saved = commandRepository.insert(show);
        return new ShowScheduleRegisterResponse(requireNonNull(saved.getId()));
    }

    @Override
    public SliceView<ShowResponse> fetchShows(Integer page, Integer size, String type, String rating,
                                              String q, LocalDate from, LocalDate to) {
        return queryRepository.fetch(page, size,
                nullableEnum(Type.class, type), nullableEnum(Rating.class, rating),
                q, from, to);
    }

    @Override
    public ShowDetailResponse fetchShowDetail(Long showId) {
        var show = queryRepository.findById(showId);
        var hall = hallFetcher.fetch(show.getHallId());
        return new ShowDetailResponse(
                show.getId(),
                show.getTitle(),
                show.getType(),
                show.getRating(),
                show.getSynopsis(),
                show.getPosterUrl(),
                show.getPerformanceStartDate(),
                show.getPerformanceEndDate(),
                hall.getId(),
                hall.getHallName(),
                show.getScheduleResponses(),
                show.getGradeResponses()
        );
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

