package com.epos.backend.service;

import java.math.BigDecimal;

import com.epos.backend.model.dto.request.RedeemPointRequest;
import com.epos.backend.model.dto.response.RedeemPointResponse;
import com.epos.backend.model.entity.TrxSales;

public interface TrxLoyaltyPointService {

    public RedeemPointResponse redeemPoint(RedeemPointRequest request);
    public void processEarnPoint(TrxSales dataSales);
    public void reverseEarnPoint(String salesNo);
    public void reverseRedeemPoint(String salesNo);
    public Long reverseEarnPointForReturn(String salesNo, String returnNo, BigDecimal returnAmount);

}
