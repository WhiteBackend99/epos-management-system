package com.epos.backend.service;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.TrxPurchaseCancelRequest;
import com.epos.backend.model.dto.request.TrxPurchaseRequest;
import com.epos.backend.model.dto.request.search.TrxPurchaseSearchRequest;
import com.epos.backend.model.dto.response.TrxPurchaseResponse;

public interface TrxPurchaseService {

    public TrxPurchaseResponse createPurchase(TrxPurchaseRequest request);
    public TrxPurchaseResponse updatePurchase(Long id, TrxPurchaseRequest request);
    public TrxPurchaseResponse completePurchase(Long id);
    public TrxPurchaseResponse cancelPurchase(Long id, TrxPurchaseCancelRequest request);
    public TrxPurchaseResponse getPurchaseById(Long id);
    public Page<TrxPurchaseResponse> searchData(int page, int size, TrxPurchaseSearchRequest request);

}
