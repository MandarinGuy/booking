package org.mandarin.booking.domain.show;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter(value = PACKAGE)
public class SeatState extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private Long gradeId;

    static SeatState create(Inventory inventory, Long seatId, Long gradeId) {
        var state = new SeatState();
        state.inventory = inventory;
        state.seatId = seatId;
        state.gradeId = gradeId;
        return state;
    }

    SeatStateRow extractRow() {
        return new SeatStateRow(seatId, gradeId);
    }

    public record SeatStateRow(Long seatId, Long gradeId) {
    }
}
