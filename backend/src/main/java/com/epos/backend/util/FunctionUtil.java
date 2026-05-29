package com.epos.backend.util;

import java.math.BigDecimal;
import java.util.List;

import com.epos.backend.model.dto.request.PosSalesPaymentSettlementRequest.PosSalesPaymentRequest;

public class FunctionUtil {

    public static BigDecimal calculateGrandTotal(BigDecimal subTotal, BigDecimal discount, BigDecimal tax) {
        BigDecimal grandTotal = subTotal.subtract(ParseUtil.defaultAmount(discount)).add(ParseUtil.defaultAmount(tax));

        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Total pembelian tidak boleh minus");
        }
        return grandTotal;
    }

    public static BigDecimal calculatePaidAmount(List<PosSalesPaymentRequest> payments) {
        return payments.stream()
            .map(PosSalesPaymentRequest::getPaidAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
