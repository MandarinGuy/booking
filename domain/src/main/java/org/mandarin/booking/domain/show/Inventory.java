package org.mandarin.booking.domain.show;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;
import org.mandarin.booking.domain.show.SeatState.SeatStateRow;


@Entity
@Table(indexes = {
        @Index(name = "idx_inventory_show_schedule_id", columnList = "show_schedule_id")
})
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Inventory extends AbstractEntity {
    @OneToMany(mappedBy = "inventory", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatState> states = new ArrayList<>();

    private Long showScheduleId;

    public List<SeatStateRow> extractSeatStateRows() {
        return states.stream()
                .map(SeatState::extractRow)
                .toList();
    }

    public void clearSeatStates() {
        this.states.clear();
    }

    public static Inventory create(Long showScheduleId, Map<Long, List<Long>> seatAssociations) {
        var inventory = new Inventory();
        inventory.showScheduleId = showScheduleId;

        var seatStates = seatAssociations.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(seat -> SeatState.create(inventory, seat, entry.getKey())))
                .toList();

        inventory.states.addAll(seatStates);
        return inventory;
    }
}
