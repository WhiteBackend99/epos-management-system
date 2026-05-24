package com.epos.backend.model.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.Type;

import com.epos.backend.enums.ReceiptReferenceType;
import com.epos.backend.enums.ReceiptStatus;
import com.epos.backend.enums.ReceiptType;

import io.hypersistence.utils.hibernate.type.json.JsonType;
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
@Table(name = "trx_receipt", schema = "public")
public class TrxReceipt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "receipt_no", nullable = false, unique = true)
    private String receiptNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false)
    private ReceiptReferenceType referenceType;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "reference_no", nullable = false)
    private String referenceNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_type", nullable = false)
    private ReceiptType receiptType;

    @Column(name = "template_version", nullable = false)
    private String templateVersion;

    @Type(JsonType.class)
    @Column(name = "receipt_payload", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> receiptPayload;

    @Column(name = "receipt_text", nullable = false, columnDefinition = "TEXT")
    private String receiptText;

    @Column(name = "print_count", nullable = false)
    private Long printCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReceiptStatus status;

    @Column(name = "first_printed_by")
    private String firstPrintedBy;

    @Column(name = "first_printed_at")
    private LocalDateTime firstPrintedAt;

    @Column(name = "lst_reprinted_by")
    private String lastReprintedBy;

    @Column(name = "last_reprinted_at")
    private LocalDateTime lastReprintedAt;

}
