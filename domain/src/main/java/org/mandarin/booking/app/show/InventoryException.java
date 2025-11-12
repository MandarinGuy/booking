package org.mandarin.booking.app.show;

import org.mandarin.booking.DomainException;

public class InventoryException extends DomainException {
    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(String status, String message) {
        super(status, message);
    }
}
