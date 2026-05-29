package com.epos.backend.model.dto.request.search;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.epos.backend.enums.PaymentMethod;
import com.epos.backend.enums.SalesStatus;

import lombok.Data;

@Data
public class PosSalesSearchRequest {

    private String keyword;
    private String salesNo;
    private String customerName;
    private String memberCode;
    private SalesStatus status;
    private PaymentMethod paymentMethod;
    private String cashierUsername;
    private Timestamp startDate;
    private Timestamp endDate;
    private BigDecimal minGrandTotal;
    private BigDecimal maxGrandTotal;
    private int page = 0;
    private int size = 10;

}
