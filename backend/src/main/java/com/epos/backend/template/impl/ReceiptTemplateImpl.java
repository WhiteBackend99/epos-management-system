package com.epos.backend.template.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.epos.backend.template.ReceiptTemplate;

@Component
public class ReceiptTemplateImpl implements ReceiptTemplate {

    private static final int WIDTH = 32;

    private String line() {
        return "-".repeat(WIDTH);
    }

    private String center(String value) {
        if (value.length() >= WIDTH) return value;
        int left = (WIDTH - value.length()) / 2;
        return " ".repeat(left) + value;
    }

    private String row(String left, String right) {
        left = left == null ? "" : left;
        right = right == null ? "" : right;
        int space = WIDTH - left.length() - right.length();
        if (space < 1) space = 1;

        return left + " ".repeat(space) + right;
    }

    private String val(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private String money(Object value) {
        if (value == null) {
            return "0";
        }

        BigDecimal amount;

        if (value instanceof BigDecimal bd) {
            amount = bd;            
        } else if (value instanceof Integer i) {
            amount = BigDecimal.valueOf(i);
        } else if (value instanceof Long l) {
            amount = BigDecimal.valueOf(l);
        } else if (value instanceof Double d) {
            amount = BigDecimal.valueOf(d);
        } else if (value instanceof Float f) {
            amount = BigDecimal.valueOf(f.doubleValue());
        } else {
            try {
                amount = new BigDecimal(String.valueOf(value));
            } catch (Exception e) {
                return "0";
            }
        }

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(amount);
    }

    @Override
    public String templateVersion() {
        return "POS_RECEIPT_V1";
    }

    @Override
    public String renderText(Map<String, Object> payload) {
        StringBuilder sb = new StringBuilder();
        sb.append(line()).append("\n");
        sb.append(center("EPOS STORE")).append("\n");
        sb.append(line()).append("\n");
        sb.append(row("Receipt", val(payload, "receiptNo"))).append("\n");
        sb.append(row("Sales", val(payload, "referenceNo"))).append("\n");
        sb.append(row("Cashier", val(payload, "cashier"))).append("\n");
        sb.append(row("Date", val(payload, "transactionDate"))).append("\n");
        sb.append(line()).append("\n");

        List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
        for (Map<String,Object> item : items) {
            sb.append(val(item, "productName")).append("\n");
            
            String left = item.get("qty") + " x " + money(item.get("price"));
            String right = money("subtotal");
            sb.append(row(left, right)).append("\n");
        }

        sb.append(line()).append("\n");
        sb.append(row("SUBTOTAL", money(payload.get("subtotal")))).append("\n");
        sb.append(row("DISCOUNT", money(payload.get("discountAmount")))).append("\n");
        sb.append(row("TAX", money(payload.get("taxAmount")))).append("\n");
        sb.append(row("TOTAL", money(payload.get("grandTotal")))).append("\n");
        sb.append(line()).append("\n");

        List<Map<String, Object>> payments = (List<Map<String, Object>>) payload.get("payments");
        for (Map<String,Object> payment : payments) {
            sb.append(row(val(payment, "method"), money(payment.get("amount")))).append("\n");
        }

        sb.append(row("PAID", money(payload.get("paidAmount")))).append("\n");
        sb.append(row("CHANGE", money(payload.get("changeAmount")))).append("\n");
        sb.append(line()).append("\n");
        sb.append(center("THANK YOU")).append("\n");
        sb.append(line());

        return sb.toString();
    }

}
