package org.mandarin.booking.domain.venue;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Hall extends AbstractEntity {
    private Long showId;

    public static Hall create(Long showId) {
        return new Hall(showId);
    }
}
