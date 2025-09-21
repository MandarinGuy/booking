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
class ShowSchedule extends AbstractEntity {

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Integer runtimeMinutes;

    private ShowSchedule(
            Show show,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer runtimeMinutes
    ) {
        this.show = show;
        this.startAt = startAt;
        this.endAt = endAt;
        this.runtimeMinutes = runtimeMinutes;
    }

    static ShowSchedule create(Show show, ShowScheduleCreateCommand command) {
        return new ShowSchedule(
                show,
                command.startAt(),
                command.endAt(),
                command.getRuntimeMinutes()
        );
    }
}
