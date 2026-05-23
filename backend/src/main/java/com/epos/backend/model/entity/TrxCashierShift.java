package com.epos.backend.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.epos.backend.enums.CashierShiftStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper=true)
@Data
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trx_cashier_shift", schema = "public")
public class TrxCashierShift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "shift_no", nullable = false, unique = true)
    private String shiftNo;

    @Column(name = "cashier_username", nullable = false)
    private String cashierUsername;

    @Column(name = "opening_cash", nullable = false)
    private BigDecimal openingCash;

    @Column(name = "expected_cash", nullable = false)
    private BigDecimal expectedCash;

    @Column(name = "actual_cash", nullable = false)
    private BigDecimal actualCash;

    @Column(name = "difference_amount", nullable = false)
    private BigDecimal differenceAmount;

    @Column(name = "total_sales_amount", nullable = false)
    private BigDecimal totalSalesAmount;

    @Column(name = "total_refund_amount", nullable = false)
    private BigDecimal totalRefundAmount;

    @Column(name = "total_cash_sales", nullable = false)
    private BigDecimal totalCashSales;

    @Column(name = "total_qris_sales", nullable = false)
    private BigDecimal totalQrisSales;

    @Column(name = "total_transfer_sales", nullable = false)
    private BigDecimal totalTransferSales;

    @Column(name = "total_debit_card_sales", nullable = false)
    private BigDecimal totalDebitCardSales;

    @Column(name = "total_cash_refund", nullable = false)
    private BigDecimal totalCashRefund;

    @Column(name = "total_qris_refund", nullable = false)
    private BigDecimal totalQrisRefund;

    @Column(name = "total_transfer_refund", nullable = false)
    private BigDecimal totalTransferRefund;

    @Column(name = "total_debit_card_refund", nullable = false)
    private BigDecimal totalDebitCardRefund;

    @Column(name = "sales_transaction_count", nullable = false)
    private Long salesTransactionCount;

    @Column(name = "return_transaction_count", nullable = false)
    private Long returnTransactionCount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CashierShiftStatus status;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "open_notes")
    private String openNotes;

    @Column(name = "close_notes")
    private String closeNotes;

}
