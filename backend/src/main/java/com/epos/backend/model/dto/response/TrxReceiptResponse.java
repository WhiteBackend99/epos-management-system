package com.epos.backend.model.dto.response;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import com.epos.backend.enums.ReceiptReferenceType;
import com.epos.backend.enums.ReceiptStatus;
import com.epos.backend.enums.ReceiptType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrxReceiptResponse {

    private Long id;
    private String receiptNo;
    private ReceiptReferenceType referenceType;
    private Long referenceId;
    private String referenceNo;
    private ReceiptType receiptType;
    private String templateVersion;
    private Map<String, Object> receiptPayload;
    private String receiptText;
    private Long printCount;
    private ReceiptStatus status;
    private String firstPrintedBy;
    private LocalDateTime firstPrintedAt;
    private String lastReprintedBy;
    private LocalDateTime lastReprintedAt;
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;

}
