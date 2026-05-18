package com.epos.backend.model.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.epos.backend.enums.PaymentMethod;
import com.epos.backend.enums.PaymentStatus;
import com.epos.backend.enums.SalesStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PosSalesResponse {

    private Long id;
    private String salesNo;
    private String customerName;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private BigDecimal paidAmount;
    private BigDecimal changeAmount;
    private SalesStatus status;
    private String createdBy;
    private Timestamp createdAt;
    private String cancelledBy;
    private Timestamp cancelledAt;
    private String cancelReason;

    private List<PosSalesDetailResponse> details;
    private List<PosSalesPaymentResponse> payments;

    @Data
    @Builder
    public static class PosSalesDetailResponse {

        private Long productId;
        private String productName;
        private BigDecimal price;
        private Long qty;
        private BigDecimal subtotal;

    }

    @Data
    @Builder
    public static class PosSalesPaymentResponse {

        private PaymentMethod paymentMethod;
        private PaymentStatus paymentStatus;
        private BigDecimal paidAmount;

    }

}
