package com.epos.backend.model.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @NotBlank(message = "Nama promo wajib diisi")
    private String promoName;

    @NotNull(message = "Tipe apply promo wajib diisi")
    private PromoApplyType applyType;

   @NotNull(message = "Tipe promo wajib diisi")
    private PromoType promoType;

    @NotNull(message = "Tanggal mulai promo wajib diisi")
    private LocalDateTime startDate;

    @NotNull(message = "Tanggal akhir promo wajib diisi")
    private LocalDateTime endDate;

    private String promoCode;
    private Long priority = 0l;
    private Boolean stackable = false;
    private Long maxUsage;
    private Long maxUsagePerCustomer;
    private String description;

    @Valid
    @NotEmpty(message = "Rule promo wajib diisi")
    private List<PromoRuleRequest> rules;

    @Valid
    @NotEmpty(message = "Reward promo wajib diisi")
    private List<PromoRewardRequest> rewards;
    
    @Data
    public static class PromoRuleRequest {
        
        @NotNull(message = "Tipe rule wajib diisi")
        private PromoRuleType ruleType;

        @NotNull(message = "Operator rule wajib diisi")
        private PromoOperator operator;

        @NotNull(message = "Nilai rule wajib diisi")
        private Map<String, Object> ruleValue;

    }

    @Data
    public static class PromoRewardRequest {
        
        @NotNull(message = "Tipe reward wajib diisi")
        private PromoRewardType rewardType;

        @NotNull(message = "Nilai reward wajib diisi")
        @DecimalMin(value = "0.01", message = "Nilai reward minimal 0.01")
        private BigDecimal rewardValue;

        private BigDecimal maxDiscountAmount;
    }
}
