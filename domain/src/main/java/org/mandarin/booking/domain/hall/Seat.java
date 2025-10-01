package org.mandarin.booking.domain.hall;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Getter(value = PACKAGE)
@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
class Seat extends AbstractEntity {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    private String rowNumber;

    private String seatNumber;
}
