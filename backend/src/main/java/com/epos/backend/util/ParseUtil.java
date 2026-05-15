package com.epos.backend.util;

public class ParseUtil {

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    public static String toLowerTrim(String value) {
        return hasText(value) ? value.trim().toLowerCase() : null;
    }

}
