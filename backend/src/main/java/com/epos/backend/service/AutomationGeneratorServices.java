package com.epos.backend.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutomationGeneratorServices {

    private final SequenceServices sequenceServices;

    private String generate(String prefix, String sequenceCode) {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long sequence = sequenceServices.nextValue(sequenceCode);
        return prefix + "-" + date + "-" + String.format("%06d", sequence);
    }

    public String generateCategoryCode() {
        return generate("CAT", "CATEGORY_CODE");
    }

    public String generateProductCode() {
        return generate("PRD", "PRODUCT_CODE");
    }

    public String generateSupplierCode() {
        return generate("SUP", "SUPPLIER_CODE");
    }

    public String generatePurchaseNo() {
        return generate("PUR", "PURCHASE_NO");
    }

    public String generateSalesNo() {
        return generate("SALE", "SALES_NO");
    }

    public String generateReturnNo() {
        return generate("RET", "RETURN_NO");
    }

    public String generateReceiptNo() {
        return generate("RCP", "RECEIPT_NO");
    }

    public String generateMemberCode() {
        return generate("MBR", "MEMBER_CODE");
    }

    public String generatePromoNo() {
        return generate("PRM", "PROMO_NO");
    }

    public String generateStockMovementNo() {
        return generate("STM", "STOCK_MOVEMENT_NO");
    }

}
