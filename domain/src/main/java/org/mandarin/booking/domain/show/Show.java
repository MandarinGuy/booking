package org.mandarin.booking.domain.show;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;
import org.mandarin.booking.domain.show.ShowDetailResponse.ShowScheduleResponse;

@Entity
@Table(name = "shows")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Show extends AbstractEntity {
    @OneToMany(mappedBy = "show", fetch = LAZY, cascade = ALL)
    private final List<ShowSchedule> schedules = new ArrayList<>();

    private Long hallId;

    private String title;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    private String synopsis;

    private String posterUrl;

    private LocalDate performanceStartDate;

    private LocalDate performanceEndDate;

    private Show(Long hallId, String title, Type type, Rating rating, String synopsis, String posterUrl,
                 LocalDate performanceStartDate,
                 LocalDate performanceEndDate) {
        this.hallId = hallId;
        this.title = title;
        this.type = type;
        this.rating = rating;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.performanceStartDate = performanceStartDate;
        this.performanceEndDate = performanceEndDate;
    }

    public void registerSchedule(ShowScheduleCreateCommand command) {
        if (!isInSchedule(command.startAt(), command.endAt())) {
            throw new ShowException("BAD_REQUEST", "공연 기간 범위를 벗어나는 일정입니다.");
        }

        var schedule = ShowSchedule.create(this, command);
        this.schedules.add(schedule);
    }

    public List<ShowDetailResponse.ShowScheduleResponse> getScheduleResponses() {
        return this.schedules.stream()
                .sorted(Comparator.comparing(ShowSchedule::getEndAt))
                .map(
                        schedule -> new ShowScheduleResponse(
                                schedule.getId(),
                                schedule.getStartAt(),
                                schedule.getEndAt()
                        )
                )
                .toList();
    }

    public static Show create(Long hallId, ShowCreateCommand command) {
        var startDate = command.getPerformanceStartDate();
        var endDate = command.getPerformanceEndDate();

        if (startDate.isAfter(endDate)) {
            throw new ShowException("공연 시작 날짜는 종료 날짜 이후에 있을 수 없습니다.");
        }

        return new Show(
                hallId,
                command.getTitle(),
                command.getType(),
                command.getRating(),
                command.getSynopsis(),
                command.getPosterUrl(),
                startDate,
                endDate
        );
    }

    private boolean isInSchedule(LocalDateTime scheduleStartAt, LocalDateTime scheduleEndAt) {
        return scheduleStartAt.isAfter(performanceStartDate.atStartOfDay())
               && scheduleEndAt.isBefore(performanceEndDate.atStartOfDay());
    }

    public enum Type {
        MUSICAL, PLAY, CONCERT, OPERA, DANCE, CLASSICAL, ETC
    }

    public enum Rating {
        ALL, AGE12, AGE15, AGE18
    }

    @Getter
    public static class ShowCreateCommand {
        private final String title;
        private final Type type;
        private final Rating rating;
        private final String synopsis;
        private final String posterUrl;
        private final LocalDate performanceStartDate;
        private final LocalDate performanceEndDate;

        private ShowCreateCommand(String title, Type type, Rating rating, String synopsis, String posterUrl,
                                  LocalDate performanceStartDate, LocalDate performanceEndDate) {
            this.title = title;
            this.type = type;
            this.rating = rating;
            this.synopsis = synopsis;
            this.posterUrl = posterUrl;
            this.performanceStartDate = performanceStartDate;
            this.performanceEndDate = performanceEndDate;
        }

        public static ShowCreateCommand from(ShowRegisterRequest request) {
            return new ShowCreateCommand(
                    request.title(),
                    Type.valueOf(request.type()),
                    Rating.valueOf(request.rating()),
                    request.synopsis(),
                    request.posterUrl(),
                    request.performanceStartDate(),
                    request.performanceEndDate()
            );
        }
    }
}

