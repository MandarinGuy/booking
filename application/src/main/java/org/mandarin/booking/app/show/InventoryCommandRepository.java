package org.mandarin.booking.app.show;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.Inventory;
import org.mandarin.booking.domain.show.SeatState.SeatStateRow;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
class InventoryCommandRepository {
    private final InventoryRepository repository;
    private final SeatStateBatchRepository seatStateBatchRepository;

    void insert(Inventory inventory) {
        List<SeatStateRow> rows = inventory.extractSeatStateRows();
        inventory.clearSeatStates();// jpa가 아닌 jdbc로 일괄 삽입하기 위해 clear

        repository.save(inventory);

        seatStateBatchRepository.batchInsert(inventory.getId(), rows);
    }
}
