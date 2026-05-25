package com.epos.backend.service;

import java.util.List;

import com.epos.backend.model.dto.AppliedPromoResult;
import com.epos.backend.model.dto.request.CreatePromoRequest;
import com.epos.backend.model.dto.request.PromoSimulationRequest;
import com.epos.backend.model.dto.request.UpdatePromoRequest;
import com.epos.backend.model.dto.response.PromoResponse;

public interface PromoService {

    public PromoResponse createPromo(CreatePromoRequest request);
    public PromoResponse updatePromo(Long id, UpdatePromoRequest request);
    public PromoResponse getDataById(Long id);
    public List<PromoResponse> getAllActive();
    public AppliedPromoResult simulate(PromoSimulationRequest request);

}
