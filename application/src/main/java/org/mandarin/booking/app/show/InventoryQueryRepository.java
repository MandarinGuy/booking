package org.mandarin.booking.app.show;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.Inventory;
import org.mandarin.booking.domain.show.SeatState.SeatStateRow;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
class InventoryQueryRepository {
    private final InventoryRepository repository;

    List<SeatStateRow> findSeatStateRowsByScheduleId(Long scheduleId) {
        return repository.findByShowScheduleId(scheduleId)
                .map(Inventory::extractSeatStateRows)
                .orElseThrow(
                        () -> new InventoryException("NOT_FOUND", "Inventory not found for scheduleId: " + scheduleId));
    }
}

