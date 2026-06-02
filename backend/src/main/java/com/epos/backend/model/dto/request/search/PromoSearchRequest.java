package com.epos.backend.model.dto.request.search;

import java.time.LocalDateTime;

import com.epos.backend.enums.PromoApplyType;
import com.epos.backend.enums.PromoStatus;
import com.epos.backend.enums.PromoType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoSearchRequest {

    private String keyword;
    private String promoNo;
    private String promoName;
    private String promoCode;
    private PromoApplyType applyType;
    private PromoType promoType;
    private PromoStatus status;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

}
