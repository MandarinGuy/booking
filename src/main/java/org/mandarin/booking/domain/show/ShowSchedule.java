package org.mandarin.booking.domain.show;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;
import org.mandarin.booking.domain.venue.Hall;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ShowSchedule extends AbstractEntity {

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Integer runtimeMinutes;

    private ShowSchedule(
            Show show,
            Hall hall,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer runtimeMinutes
    ) {
        this.show = show;
        this.hall = hall;
        this.startAt = startAt;
        this.endAt = endAt;
        this.runtimeMinutes = runtimeMinutes;
    }

    public boolean isConflict(LocalDateTime startAt, LocalDateTime endAt) {
        return startAt.isBefore(this.endAt)
               && endAt.isAfter(this.startAt);
    }

    static ShowSchedule create(Show show, Hall hall, ShowScheduleCreateCommand command) {
        return new ShowSchedule(
                show,
                hall,
                command.startAt,
                command.endAt,
                (int) Duration.between(command.startAt, command.endAt).toMinutes()
        );
    }

    public record ShowScheduleCreateCommand(Long showId, LocalDateTime startAt, LocalDateTime endAt) {
    }
}
