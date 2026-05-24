package com.epos.backend.model.dto.request.search;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.epos.backend.enums.PaymentMethod;

import lombok.Data;

@Data
public class ReportSearchRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    
    private String cashierUsername;
    private String shiftNo;
    private PaymentMethod paymentMethod;
    private Long productId;
    private String keyword;
}
