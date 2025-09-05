package org.mandarin.booking.domain.show;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ShowSchedule extends AbstractEntity {

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    private Long hallId;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Integer runtimeMinutes;

    private ShowSchedule(
            Show show,
            Long hallId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer runtimeMinutes
    ) {
        this.show = show;
        this.hallId = hallId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.runtimeMinutes = runtimeMinutes;
    }

    public static ShowSchedule create(Show show, ShowScheduleCreateCommand command) {
        return new ShowSchedule(
                show,
                command.hallId,
                command.startAt,
                command.endAt,
                command.runtimeMinutes
        );
    }


    @Getter
    public static class ShowScheduleCreateCommand {
        private final Long showId;
        private final Long hallId;
        private final LocalDateTime startAt;
        private final LocalDateTime endAt;
        private final Integer runtimeMinutes;

        private ShowScheduleCreateCommand(
                Long showId,
                Long hallId,
                LocalDateTime startAt,
                LocalDateTime endAt,
                Integer runtimeMinutes
        ) {
            this.showId = showId;
            this.hallId = hallId;
            this.startAt = startAt;
            this.endAt = endAt;
            this.runtimeMinutes = runtimeMinutes;
        }

        public static ShowScheduleCreateCommand from(ShowScheduleRegisterRequest request) {
            return new ShowScheduleCreateCommand(
                    request.showId(),
                    request.hallId(),
                    request.startAt(),
                    request.endAt(),
                    request.runtimeMinutes()
            );
        }
    }
}
