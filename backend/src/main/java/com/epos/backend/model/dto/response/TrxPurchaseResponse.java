package com.epos.backend.model.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.epos.backend.enums.PurchaseStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrxPurchaseResponse {

    private Long id;
    private String purchaseNo;
    private String invoiceNo;
    private Long supplierId;
    private String supplierName;
    private Date purchaseDate;
    private PurchaseStatus status;
    private BigDecimal subTotalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotalAmount;
    private String notes;
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
    private String completedBy;
    private Timestamp completedAt;
    private String cancelledBy;
    private Timestamp cancelledAt;
    private String cancelReason;

    private List<TrxPurchaseDetailRespose> items; 

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrxPurchaseDetailRespose {

        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private Long qty;
        private BigDecimal purchasePrice;
        private BigDecimal subTotal;

    }

}
