package org.mandarin.booking.app.show;

import java.util.Optional;
import org.mandarin.booking.domain.show.Inventory;
import org.springframework.data.repository.Repository;

interface InventoryRepository extends Repository<Inventory, Long> {
    void save(Inventory inventory);

    Optional<Inventory> findByShowScheduleId(Long showScheduleId);
}
