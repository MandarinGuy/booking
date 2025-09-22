package org.mandarin.booking.utils;

public class EnumFixture {
    public static <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        return enumClass.getEnumConstants()[(int) (Math.random() * enumClass.getEnumConstants().length)];
    }
}
