package com.epos.backend.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

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

    public static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    public static String formatDateToStr(Timestamp dateTime) {
        return dateTime == null ? "" : dateTime.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public static BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
    
    public static Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

}
