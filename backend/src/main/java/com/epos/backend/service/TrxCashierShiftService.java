package com.epos.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epos.backend.model.dto.request.CloseCashierShiftRequest;
import com.epos.backend.model.dto.request.OpenCashierShiftRequest;
import com.epos.backend.model.dto.request.search.TrxCashierShiftSearchRequest;
import com.epos.backend.model.dto.response.TrxCashierShiftResponse;

public interface TrxCashierShiftService {

    public TrxCashierShiftResponse openShift(OpenCashierShiftRequest request);
    public TrxCashierShiftResponse closeShift(CloseCashierShiftRequest request);
    public TrxCashierShiftResponse getCurrentOpenShift();
    public TrxCashierShiftResponse getDataById(Long id);
    public TrxCashierShiftResponse getByShiftNo(String shiftNo);
    public Page<TrxCashierShiftResponse> searchData(Pageable pageable, TrxCashierShiftSearchRequest request);

}
