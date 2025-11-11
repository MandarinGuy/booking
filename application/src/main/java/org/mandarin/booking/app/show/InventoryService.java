package org.mandarin.booking.app.show;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.Inventory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class InventoryService implements InventoryWriter {
    private final InventoryCommandRepository commandRepository;

    @Override
    public void createInventory(Long scheduleId, Map<Long, List<Long>> seatAssociations) {
        Inventory inventory = Inventory.create(scheduleId, seatAssociations);
        commandRepository.insert(inventory);
    }
}
