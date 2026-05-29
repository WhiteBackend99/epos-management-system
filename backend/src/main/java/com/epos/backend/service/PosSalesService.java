package com.epos.backend.service;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.PosSalesCancelRequest;
import com.epos.backend.model.dto.request.PosSalesPaymentSettlementRequest;
import com.epos.backend.model.dto.request.PosSalesRequest;
import com.epos.backend.model.dto.request.search.PosSalesSearchRequest;
import com.epos.backend.model.dto.response.PosSalesResponse;

public interface PosSalesService {

    public PosSalesResponse createDraftSales(PosSalesRequest request);
    public PosSalesResponse settlePayment(String salesNo, PosSalesPaymentSettlementRequest request);
    public PosSalesResponse getSalesById(Long id);
    public PosSalesResponse getSalesByNo(String salesNo);
    public PosSalesResponse cancelSales(Long id, PosSalesCancelRequest request);
    public Page<PosSalesResponse> searchData(PosSalesSearchRequest request);
}
