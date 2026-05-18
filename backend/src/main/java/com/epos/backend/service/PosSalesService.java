package com.epos.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epos.backend.model.dto.request.PosSalesCancelRequest;
import com.epos.backend.model.dto.request.PosSalesRequest;
import com.epos.backend.model.dto.response.PosSalesResponse;

public interface PosSalesService {

    public PosSalesResponse createSales(PosSalesRequest request);
    public PosSalesResponse getSalesById(Long id);
    public PosSalesResponse getSalesByNo(String salesNo);
    public PosSalesResponse cancelSales(Long id, PosSalesCancelRequest request);
    public Page<PosSalesResponse> searchData(String keyword, Pageable pageable);
}
