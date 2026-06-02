package com.epos.backend.service;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.SupplierRequest;
import com.epos.backend.model.dto.request.search.SupplierSearchRequest;
import com.epos.backend.model.dto.response.SupplierResponse;

public interface SupplierService {

    public SupplierResponse createSupplier(SupplierRequest request);
    public SupplierResponse updateSupplier(Long id, SupplierRequest request);
    public SupplierResponse getSupplierById(Long id);
    public SupplierResponse deleteSupplier(Long id);
    public SupplierResponse activateSupplier(Long id);
    public SupplierResponse deactivateSupplier(Long id);
    public Page<SupplierResponse> searchData(SupplierSearchRequest request);

}
