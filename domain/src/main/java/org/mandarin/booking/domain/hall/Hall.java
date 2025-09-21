package org.mandarin.booking.domain.hall;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Hall extends AbstractEntity {
    private String name;

    public static Hall create(String name) {
        return new Hall(name);
    }
}
