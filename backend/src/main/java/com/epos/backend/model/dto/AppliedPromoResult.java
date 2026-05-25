package com.epos.backend.model.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppliedPromoResult {

    private Boolean applied;
    private Long promoId;
    private String promoNo;
    private String promoName;
    private String promoCode;
    private BigDecimal discountAmount;
    private Map<String, Object> promoSnapshot;

}
