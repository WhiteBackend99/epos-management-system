package com.epos.backend.model.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.epos.backend.enums.RefundMethod;
import com.epos.backend.enums.RefundStatus;
import com.epos.backend.enums.SalesReturnStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrxSalesReturnResponse {

    private Long id;
    private String returnNo;
    private Long salesId;
    private String salesNo;
    private String customerName;
    private BigDecimal totalReturnAmount;
    private BigDecimal refundAmount;
    private SalesReturnStatus returnStatus;
    private RefundStatus refundStatus;
    private String returnReason;
    private String createdBy;
    private Timestamp createdAt;
    private String cancelledBy;
    private Timestamp cancelledAt;
    private String cancelReason;

    private List<TrxSalesReturnDetailResponse> details;
    private List<TrxSalesReturnRefundPaymentResponse> refundPayments;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrxSalesReturnDetailResponse {

        private Long salesDetailId;
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Long qtySold;
        private Long qtyReturn;
        private BigDecimal subtotalReturn;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrxSalesReturnRefundPaymentResponse {

        private RefundMethod refundMethod;
        private BigDecimal refundAmount;
        private RefundStatus refundStatus;
        private String createdBy;
        private Timestamp createdAt;
    }

}
