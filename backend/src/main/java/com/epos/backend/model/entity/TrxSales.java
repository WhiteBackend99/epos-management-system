package com.epos.backend.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.epos.backend.enums.SalesStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trx_sales", schema = "public")
public class TrxSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sales_no", nullable = false, unique = true)
    private String salesNo;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "subtotal", precision = 18, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "tax_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal taxAmount;

    @Column(name = "grand_total", precision = 18, scale = 2, nullable = false)
    private BigDecimal grandTotal;

    @Column(name = "paid_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal paidAmount;

    @Column(name = "change_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal changeAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SalesStatus status;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "cancelled_at")
    private Timestamp cancelledAt;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "promo_discount_amount", nullable = false)
    private BigDecimal promoDiscountAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_shift_id")
    private TrxCashierShift cashierShift;;

    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrxSalesDetail> details;

    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrxSalesPayment> payments;

}
