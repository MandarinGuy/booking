package org.mandarin.booking;

public final class StringFormatterUtils {
    private StringFormatterUtils() {
    }

    public static String toSnakeCase(String s) {
        return s.replaceAll("([a-z])([A-Z])", "$1_$2") // camelCase → camel_Case
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2") // HTTPServer → HTTP_Server
                .toLowerCase();
    }
}
