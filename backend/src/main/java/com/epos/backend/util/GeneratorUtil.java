package com.epos.backend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class GeneratorUtil {

    private static final AtomicLong COUNTER = new AtomicLong(1);

    public static String generateSku() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = COUNTER.getAndIncrement();
        return String.format("PRD-%s-%04d", date, sequence);
    }

}
