package com.epos.backend.model.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.epos.backend.enums.PromoApplyType;
import com.epos.backend.enums.PromoOperator;
import com.epos.backend.enums.PromoRewardType;
import com.epos.backend.enums.PromoRuleType;
import com.epos.backend.enums.PromoType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePromoRequest {

    @NotBlank
    private String promoName;

    private String promoCode;

    @NotNull
    private PromoApplyType applyType;

    @NotNull
    private PromoType promoType;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private Long priority = 0l;
    private Boolean stackable = false;
    private Long maxUsage;
    private Long maxUsagePerCustomer;
    private String description;

    @Valid
    @NotEmpty
    private List<PromoRuleRequest> rules;

    @Valid
    @NotEmpty
    private List<PromoRewardRequest> rewards;
    
    @Data
    public static class PromoRuleRequest {
        @NotNull
        private PromoRuleType ruleType;

        @NotNull
        private PromoOperator operator;

        @NotNull
        private Map<String, Object> ruleValue;
    }

    @Data
    public static class PromoRewardRequest {
        
        @NotNull
        private PromoRewardType rewardType;

        @NotNull
        @DecimalMin("0.01")
        private BigDecimal rewardValue;

        private BigDecimal maxDiscountAmount;
    }
}
