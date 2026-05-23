package com.epos.backend.model.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.epos.backend.enums.CashierShiftStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrxCashierShiftResponse {

    private Long id;
    private String shiftNo;
    private String cashierUsername;
    private BigDecimal openingCash;
    private BigDecimal expectedCash;
    private BigDecimal actualCash;
    private BigDecimal differenceAmount;
    private BigDecimal totalSalesAmount;
    private BigDecimal totalRefundAmount;
    private BigDecimal totalCashSales;
    private BigDecimal totalQrisSales;
    private BigDecimal totalTransferSales;
    private BigDecimal totalDebitCardSales;
    private BigDecimal totalCashRefund;
    private BigDecimal totalQrisRefund;
    private BigDecimal totalTransferRefund;
    private BigDecimal totalDebitCardRefund;
    private Long salesTransactionCount;
    private Long returnTransactionCount;
    private CashierShiftStatus status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private String openNotes;
    private String closeNotes;
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;

}
