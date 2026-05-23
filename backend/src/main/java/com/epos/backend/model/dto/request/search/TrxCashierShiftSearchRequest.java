package com.epos.backend.model.dto.request.search;

import java.time.LocalDateTime;

import com.epos.backend.enums.CashierShiftStatus;

import lombok.Data;

@Data
public class TrxCashierShiftSearchRequest {

    private String keyword;
    private CashierShiftStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
