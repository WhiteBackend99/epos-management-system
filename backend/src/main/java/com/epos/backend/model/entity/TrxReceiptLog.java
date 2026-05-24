package com.epos.backend.model.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.epos.backend.enums.ReceiptAction;

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
@Table(name = "trx_receipt_log", schema = "public")
public class TrxReceiptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receipt_id", nullable = false)
    private TrxReceipt receipt;

    @Column(name = "receipt_no", nullable = false)
    private String receiptNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private ReceiptAction action;

    @Column(name = "printed_by")
    private String printedBy;

    @Column(name = "printed_at", nullable = false)
    private LocalDateTime printedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

}
