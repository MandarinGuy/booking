package org.mandarin.booking.domain.venue;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;
import org.mandarin.booking.domain.show.ShowSchedule;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hall extends AbstractEntity {
    @OneToMany(mappedBy = "hall")
    private List<ShowSchedule> showSchedules = new ArrayList<>();

    public boolean canScheduleOn(LocalDateTime startAt, LocalDateTime endAt) {
        return showSchedules.stream()
                .noneMatch(schedule -> schedule.isConflict(startAt, endAt));
    }

    public static Hall create() {
        return new Hall();
    }
}
