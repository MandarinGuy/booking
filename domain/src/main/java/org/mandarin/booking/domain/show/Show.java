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
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.Currency;
import org.mandarin.booking.domain.AbstractEntity;
import org.mandarin.booking.domain.hall.HallException;
import org.mandarin.booking.domain.show.ShowDetailResponse.ShowScheduleResponse;
import org.mandarin.booking.domain.show.ShowRegisterRequest.GradeRequest;

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

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @OneToMany(mappedBy = "show", fetch = LAZY, cascade = ALL)
    private List<Grade> grades = new ArrayList<>();

    private Show(Long hallId, String title, Type type, Rating rating, String synopsis, String posterUrl,
                 LocalDate performanceStartDate,
                 LocalDate performanceEndDate,
                 Currency currency) {
        this.hallId = hallId;
        this.title = title;
        this.type = type;
        this.rating = rating;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.performanceStartDate = performanceStartDate;
        this.performanceEndDate = performanceEndDate;
        this.currency = currency;
    }

    public static Show create(Long hallId, ShowCreateCommand command) {
        var startDate = command.getPerformanceStartDate();
        var endDate = command.getPerformanceEndDate();

        if (startDate.isAfter(endDate)) {
            throw new ShowException("공연 시작 날짜는 종료 날짜 이후에 있을 수 없습니다.");
        }

        var show = new Show(
                hallId,
                command.getTitle(),
                command.getType(),
                command.getRating(),
                command.getSynopsis(),
                command.getPosterUrl(),
                startDate,
                endDate,
                command.getCurrency()
        );

        var grades = command.getTicketGrades().stream()
                .map(gradeReq -> Grade.of(show, gradeReq))
                .toList();
        show.addGrades(grades);
        return show;
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
                                schedule.getEndAt(),
                                schedule.getRuntimeMinutes()
                        )
                )
                .toList();
    }

    public List<GradeResponse> getGradeResponses() {
        return this.grades.stream()
                .map(Grade::toResponse)
                .sorted(Comparator.comparing(GradeResponse::basePrice)
                        .thenComparing(GradeResponse::quantity, Comparator.reverseOrder()))
                .toList();
    }

    public void validateGradeIds(List<Long> gradeIds) {
        var fetchedGradeIds = this.grades.stream()
                .map(AbstractEntity::getId).toList();
        if (!new HashSet<>(fetchedGradeIds).containsAll(gradeIds)) {
            throw new HallException("NOT_FOUND", "해당하는 등급이 존재하지 않습니다.");
        }
    }

    private void addGrades(List<Grade> grades) {
        this.grades.addAll(grades);
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
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ShowCreateCommand {

        private final String title;
        private final Type type;
        private final Rating rating;
        private final String synopsis;
        private final String posterUrl;
        private final LocalDate performanceStartDate;
        private final LocalDate performanceEndDate;
        private final Currency currency;
        private final List<GradeRequest> ticketGrades;

        public static ShowCreateCommand from(
                ShowRegisterRequest request) {//TODO 2025 10 03 00:26:26 : test 코드를 위해 public...?
            return new ShowCreateCommand(
                    request.title(),
                    Type.valueOf(request.type()),
                    Rating.valueOf(request.rating()),
                    request.synopsis(),
                    request.posterUrl(),
                    request.performanceStartDate(),
                    request.performanceEndDate(),
                    Currency.valueOf(request.currency()),
                    request.ticketGrades()
            );
        }
    }
}

