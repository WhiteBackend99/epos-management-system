package com.epos.backend.service;

import com.epos.backend.model.dto.AppliedPromoResult;
import com.epos.backend.model.dto.PromoCalculationContext;

public interface PromoEngineService {

    public AppliedPromoResult calculateBestPromo(PromoCalculationContext context);
    public AppliedPromoResult calculateManualCodePromo(PromoCalculationContext context, String promoCode);

}
