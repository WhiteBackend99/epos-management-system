package com.epos.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.CreatePromoRequest;
import com.epos.backend.model.dto.request.PromoSimulationRequest;
import com.epos.backend.model.dto.request.UpdatePromoRequest;
import com.epos.backend.model.dto.request.search.PromoSearchRequest;
import com.epos.backend.model.dto.response.PromoResponse;
import com.epos.backend.model.dto.result.AppliedPromoResult;

public interface PromoService {

    public PromoResponse createPromo(CreatePromoRequest request);
    public PromoResponse updatePromo(Long id, UpdatePromoRequest request);
    public PromoResponse getDataById(Long id);
    public PromoResponse deletePromo(Long id);
    public PromoResponse activatePromo(Long id);
    public PromoResponse deactivatePromo(Long id);
    public AppliedPromoResult simulate(PromoSimulationRequest request);
    public List<PromoResponse> getAllActive();
    public Page<PromoResponse> searchData(PromoSearchRequest request);

}
