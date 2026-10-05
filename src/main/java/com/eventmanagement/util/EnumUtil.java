package com.eventmanagement.util;

public final class EnumUtil {
    private EnumUtil() { }
    public static <T extends Enum<T>> T valueOf(Class<T> type, String value, T fallback) {
        if (value == null || value.isBlank()) return fallback;
        try { return Enum.valueOf(type, value.toUpperCase()); }
        catch (IllegalArgumentException e) { return fallback; }
    }
}
