package com.epos.backend.model.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedeemPointResponse {

    private String salesNo;
    private String memberCode;
    private Long redeemPoint;
    private BigDecimal redeemAmount;
    private Long remainingPoint;
    
}
