package org.mandarin.booking.app.show;

import org.mandarin.booking.domain.show.Inventory;
import org.springframework.data.repository.Repository;

interface InventoryRepository extends Repository<Inventory, Long> {
    void save(Inventory inventory);
}
