package com.epos.backend.model.dto.request.search;

import java.time.LocalDate;

import com.epos.backend.enums.LoyaltyPointType;

import lombok.Data;

@Data
public class TrxLoyaltyPointHistorySearchRequest {

    private LoyaltyPointType pointType;
    private String referenceNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page = 0;
    private Integer size = 10;
    
}
