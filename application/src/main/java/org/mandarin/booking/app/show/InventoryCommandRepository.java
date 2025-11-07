package org.mandarin.booking.app.show;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.JdbcBatchUtils;
import org.mandarin.booking.domain.show.Inventory;
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
        String sql = "INSERT INTO seat_state (inventory_id, seat_id, grade_id) VALUES (?, ?, ?)";
        jdbcBatchUtils.batchUpdate(
                sql,
                rows,
                (ps, row) -> {
                    ps.setLong(1, inventoryId);
                    ps.setLong(2, row.seatId());
                    ps.setLong(3, row.gradeId());
                },
                1000
        );
    }
}
