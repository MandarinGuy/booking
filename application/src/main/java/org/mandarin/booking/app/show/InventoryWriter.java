package org.mandarin.booking.app.show;

import java.util.List;
import java.util.Map;

public interface InventoryWriter {
    void createInventory(Long scheduleId, Map<Long, List<Long>> seatAssociations);
}
