package org.mandarin.booking.app.show;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.show.Inventory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
class InventoryCommandRepository {
    private final InventoryRepository repository;

    void insert(Inventory inventory) {
        repository.save(inventory);
    }
}
