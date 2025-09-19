package org.mandarin.booking.domain;

import org.jspecify.annotations.Nullable;

public final class EnumUtils {
    public static <T extends Enum<T>> T nullableEnum(Class<T> enumClass, @Nullable String value) {
        if (value == null) {
            return null;
        }
        return Enum.valueOf(enumClass, value);
    }
}
