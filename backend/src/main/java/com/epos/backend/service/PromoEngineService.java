package com.epos.backend.service;

import com.epos.backend.model.dto.context.PromoCalculationContext;
import com.epos.backend.model.dto.result.AppliedPromoResult;

public interface PromoEngineService {

    public AppliedPromoResult calculateBestPromo(PromoCalculationContext context);
    public AppliedPromoResult calculateManualCodePromo(PromoCalculationContext context, String promoCode);

}
