package org.mandarin.booking.domain.show;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.FetchType.LAZY;
import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;
import org.mandarin.booking.domain.show.ShowSchedule.ShowScheduleCreateCommand;
import org.mandarin.booking.domain.venue.Hall;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Show extends AbstractEntity {
    private String title;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    private String synopsis;

    private String posterUrl;

    private LocalDate performanceStartDate;

    private LocalDate performanceEndDate;

    @OneToMany(mappedBy = "show", fetch = LAZY, cascade = MERGE)
    private final List<ShowSchedule> schedules = new ArrayList<>();

    private Show(String title, Type type, Rating rating, String synopsis, String posterUrl,
                 LocalDate performanceStartDate,
                 LocalDate performanceEndDate) {
        this.title = title;
        this.type = type;
        this.rating = rating;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.performanceStartDate = performanceStartDate;
        this.performanceEndDate = performanceEndDate;
    }


    public static Show create(ShowCreateCommand command) {
        var startDate = command.getPerformanceStartDate();
        var endDate = command.getPerformanceEndDate();

        if (startDate.isAfter(endDate)) {
            throw new ShowException("공연 시작 날짜는 종료 날짜 이후에 있을 수 없습니다.");
        }

        return new Show(
                command.getTitle(),
                command.getType(),
                command.getRating(),
                command.getSynopsis(),
                command.getPosterUrl(),
                startDate,
                endDate
        );
    }

    public void registerSchedule(Hall hall, ShowScheduleCreateCommand command) {
        if (!isInSchedule(command.startAt(), command.endAt())) {
            throw new ShowException(BAD_REQUEST, "공연 기간 범위를 벗어나는 일정입니다.");
        }

        var schedule = ShowSchedule.create(this, hall, command);
        this.schedules.add(schedule);
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

