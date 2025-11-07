package org.mandarin.booking.app.hall;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.JdbcBatchUtils;
import org.mandarin.booking.domain.hall.Hall;
import org.mandarin.booking.domain.hall.Hall.SeatInsertRow;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
class HallCommandRepository {
    private final HallRepository jpaRepository;
    private final JdbcBatchUtils jdbcBatchUtils;

    Hall insert(Hall hall) {
        var seatRows = hall.extractSeatRows();
        hall.clearSeats();// jpa가 아닌 jdbc로 일괄 삽입을 위해 clear

        var saved = jpaRepository.save(hall);

        batchInsertSeats(seatRows);

        return saved;
    }

    private void batchInsertSeats(List<SeatInsertRow> rows) {
        String sql = "INSERT INTO seat (section_id, seat_row, seat_number) VALUES (?, ?, ?)";
        jdbcBatchUtils.batchUpdate(
                sql,
                rows,
                (ps, row) -> {
                    ps.setLong(1, row.section().getId());
                    ps.setString(2, row.rowNumber());
                    ps.setString(3, row.seatNumber());
                },
                1000
        );
    }
}
