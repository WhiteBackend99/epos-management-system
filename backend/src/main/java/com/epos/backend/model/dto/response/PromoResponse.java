package com.epos.backend.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.epos.backend.enums.PromoApplyType;
import com.epos.backend.enums.PromoOperator;
import com.epos.backend.enums.PromoRewardType;
import com.epos.backend.enums.PromoRuleType;
import com.epos.backend.enums.PromoStatus;
import com.epos.backend.enums.PromoType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromoResponse {

    private Long id;
    private String promoNo;
    private String promoName;
    private String promoCode;
    private PromoApplyType applyType;
    private PromoType promoType;
    private PromoStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long priority;
    private Boolean stackable;
    private Long maxUsage;
    private Long currentUsage;
    private Long maxUsagePerCustomer;
    private String description;

    private List<PromoRuleResponse> rules;
    private List<PromoRewardResponse> rewards;
    
    @Data
    @Builder
    public static class PromoRuleResponse {
        
        private Long id;
        private PromoRuleType ruleType;
        private PromoOperator operator;
        private Map<String, Object> ruleValue;
        
    }

    @Data
    @Builder
    public static class PromoRewardResponse {
        
        private Long id;
        private PromoRewardType rewardType;
        private BigDecimal rewardValue;
        private BigDecimal maxDiscountAmount;
        
    }
    
}
