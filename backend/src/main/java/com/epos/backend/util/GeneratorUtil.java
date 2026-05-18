package com.epos.backend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GeneratorUtil {

    private static final AtomicLong COUNTER = new AtomicLong(1);
    private static final AtomicLong COUNTER_STOCK_IN = new AtomicLong(1);
    private static final AtomicLong COUNTER_STOCK_OUT = new AtomicLong(1);
    private static final AtomicLong COUNTER_STOCK_ADJ = new AtomicLong(1);
    private static final AtomicLong COUNTER_STOCK_PURCHASE = new AtomicLong(1);

    public static String generateSku() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = COUNTER.getAndIncrement();
        return String.format("PRD-%s-%04d", date, sequence);
    }

    public static String generateReferenceNoStockIn() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = COUNTER_STOCK_IN.getAndIncrement();
        return String.format("STI-%s-%06d", date, sequence);
    }

    public static String generateReferenceNoStockOut() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = COUNTER_STOCK_OUT.getAndIncrement();
        return String.format("STO-%s-%06d", date, sequence);
    }

    public static String generateReferenceNoStockAdjustment() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = COUNTER_STOCK_ADJ.getAndIncrement();
        return String.format("STA-%s-%06d", date, sequence);
    }

    public static String generateSupplierCode(Long sequence) {
        return String.format("SUP-%06d", sequence);
    }

    public static String generatePurchaseNo(Long sequence) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("PUR-%s-%06d", date, sequence);
    }

    public static String generateInvoiceNo(Long sequence) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("PINV-%s-%06d", date, sequence);
    }

    public static String generateStockPurchaseReferenceNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = COUNTER_STOCK_PURCHASE.getAndIncrement();
        return String.format("STP-%s-%06d", date, sequence);
    }

    public static String generateSalesNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("SALE-%s-%s", date, random);
    }

}
