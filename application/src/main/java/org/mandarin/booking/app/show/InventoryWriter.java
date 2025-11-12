package org.mandarin.booking.app.show;

import java.util.List;
import java.util.Map;

public interface InventoryWriter {
    void createInventory(Long scheduleId, Map<org.mandarin.booking.domain.show.GradeMeta, List<Long>> seatAssociations);
}
