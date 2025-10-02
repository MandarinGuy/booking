package org.mandarin.booking.utils;

import java.util.UUID;

public class HallFixture {
    public static String generateHallName() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
