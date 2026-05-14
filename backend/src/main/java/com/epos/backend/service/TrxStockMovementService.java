package com.epos.backend.service;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.TrxStockAdjustmentRequest;
import com.epos.backend.model.dto.request.TrxStockInRequest;
import com.epos.backend.model.dto.request.TrxStockOutRequest;
import com.epos.backend.model.dto.response.TrxStockMovementResponse;

public interface TrxStockMovementService {

    public TrxStockMovementResponse stockIn(TrxStockInRequest request);
    public TrxStockMovementResponse stockOut(TrxStockOutRequest request);
    public TrxStockMovementResponse adjustment(TrxStockAdjustmentRequest request);
    public TrxStockMovementResponse getDataById(Long id);
    public Page<TrxStockMovementResponse> searchData(int page, int size, String search);
}
