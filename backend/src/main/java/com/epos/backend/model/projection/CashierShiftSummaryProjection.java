package com.epos.backend.model.projection;

import java.math.BigDecimal;

public interface CashierShiftSummaryProjection {

    public BigDecimal getTotalSalesAmount();
    public BigDecimal getTotalCashSales();
    public BigDecimal getTotalQrisSales();
    public BigDecimal getTotalTransferSales();
    public BigDecimal getTotalDebitCardSales();
    public Long getSalesTransactionCount();
    public BigDecimal getTotalRefundAmount();
    public BigDecimal getTotalCashRefund();
    public BigDecimal getTotalQrisRefund();
    public BigDecimal getTotalTransferRefund();
    public BigDecimal getTotalDebitCardRefund();
    public Long getReturnTransactionCount();

}
