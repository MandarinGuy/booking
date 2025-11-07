package org.mandarin.booking.app.show;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.JdbcBatchUtils;
import org.mandarin.booking.domain.EntityInsertBuilder;
import org.mandarin.booking.domain.show.Inventory;
import org.mandarin.booking.domain.show.SeatState;
import org.mandarin.booking.domain.show.SeatState.SeatStateRow;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
class InventoryCommandRepository {
    private final InventoryRepository repository;
    private final JdbcBatchUtils jdbcBatchUtils;

    void insert(Inventory inventory) {
        List<SeatStateRow> rows = inventory.extractSeatStateRows();
        inventory.clearSeatStates();// jpa가 아닌 jdbc로 일괄 삽입하기 위해 clear

        repository.save(inventory);

        batchInsert(inventory.getId(), rows);
    }

    void batchInsert(Long inventoryId, List<SeatStateRow> rows) {
        var compiled = EntityInsertBuilder.forTable("seat_state", SeatStateRow.class, SeatState.class)
                .withForeignKey(inventoryId)
                .autoBindAll()
                .compile();
        jdbcBatchUtils.batchUpdate(compiled.sql(), rows, (ps, row) -> compiled.binder().bind(ps, row), 1000);
    }
}
