package com.epos.backend.model.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesDashboardReportResponse {

    private DailySalesSummaryResponse dailySummary;
    private PaymentMethodSummaryResponse paymentSummary;
    private RefundSummaryResponse refundSummary;
    private List<CashierReportResponse> cashierReports;
    private List<ProductSalesReportResponse> productSales;

    @Data
    @Builder
    public static class DailySalesSummaryResponse {

        private BigDecimal grossSales;
        private BigDecimal totalRefund;
        private BigDecimal netSales;
        private BigDecimal averangeTransactoin;
        private Long successTransactionCount;
        private Long cancelledTransactionCount;
        private Long returnTransactionCount;

    }

    @Data
    @Builder
    public static class PaymentMethodSummaryResponse {

        private BigDecimal cash;
        private BigDecimal qris;
        private BigDecimal transfer;
        private BigDecimal debit;
        private BigDecimal total;

    }

    @Data
    @Builder
    public static class RefundSummaryResponse {

        private BigDecimal totalRefund;
        private BigDecimal cashRefund;
        private BigDecimal qrisRefund;
        private BigDecimal transferRefund;
        private BigDecimal debitRefund;
        private Long refundTransactionCount;

    }

    @Data
    @Builder
    public static class CashierReportResponse {

        private String shiftNo;
        private String cashierUsername;
        private BigDecimal openingCash;
        private BigDecimal expectedCash;
        private BigDecimal actualCash;
        private BigDecimal differenceAmount;
        private BigDecimal totalSalesAmount;
        private BigDecimal totalRefundAmount;
        private Long salesTransactionCount;
        private Long returnTransactionCount;
        private String status;

    }

    @Data
    @Builder
    public static class ProductSalesReportResponse {

        private Long productId;
        private String productName;
        private Long qtySold;
        private Long qtyReturned;
        private Long netQtySold;
        private BigDecimal grossSales;
        private BigDecimal returnAmount;
        private BigDecimal netSales;

    }

}
