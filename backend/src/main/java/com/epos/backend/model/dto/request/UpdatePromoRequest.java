package com.epos.backend.model.dto.request;

import com.epos.backend.enums.PromoStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class UpdatePromoRequest extends CreatePromoRequest {

    private PromoStatus status;
    
}
