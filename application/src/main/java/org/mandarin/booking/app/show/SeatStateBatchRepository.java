package org.mandarin.booking.app.show;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.JdbcBatchUtils;
import org.mandarin.booking.domain.show.SeatState.SeatStateRow;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class SeatStateBatchRepository {
    private final JdbcBatchUtils jdbcBatchUtils;

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
