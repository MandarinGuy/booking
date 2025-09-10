package org.mandarin.booking.domain.venue;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hall extends AbstractEntity {

//    public boolean canScheduleOn(LocalDateTime startAt, LocalDateTime endAt) {
//        return showSchedules.stream()
//                .noneMatch(schedule -> schedule.isConflict(startAt, endAt));
//    }

    public static Hall create() {
        return new Hall();
    }
}
