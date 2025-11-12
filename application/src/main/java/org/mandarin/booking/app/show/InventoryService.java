package org.mandarin.booking.app.show;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.GradeMeta;
import org.mandarin.booking.domain.show.Inventory;
import org.mandarin.booking.domain.show.SeatStatus;
import org.mandarin.booking.domain.show.SeatStatusResponse;
import org.mandarin.booking.domain.show.SeatsResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class InventoryService implements InventoryWriter, InventoryReader {
    private final InventoryCommandRepository commandRepository;
    private final InventoryQueryRepository queryRepository;

    @Override
    public void createInventory(Long scheduleId, Map<GradeMeta, List<Long>> seatAssociations) {
        Inventory inventory = Inventory.create(scheduleId, seatAssociations);
        commandRepository.insert(inventory);
    }

    @Override
    public SeatsResponse fetchSeats(Long scheduleId) {
        var rows = queryRepository.findSeatStateRowsByScheduleId(scheduleId);
        var contents = rows.stream()
                .map(r -> new SeatStatusResponse(r.seatId(), r.gradeId(), SeatStatus.AVAILABLE))
                .toList();
        return new SeatsResponse(contents);
    }
}
