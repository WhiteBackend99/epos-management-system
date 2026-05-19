package com.epos.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epos.backend.model.dto.request.TrxSalesReturnCancelRequest;
import com.epos.backend.model.dto.request.TrxSalesReturnRequest;
import com.epos.backend.model.dto.request.search.TrxSalesReturnSearchRequest;
import com.epos.backend.model.dto.response.TrxSalesReturnResponse;

public interface TrxSalesReturnService {

    public TrxSalesReturnResponse createReturn(TrxSalesReturnRequest request);
    public TrxSalesReturnResponse getDataById(Long id);
    public TrxSalesReturnResponse getByReturnNo(String returnNo);
    public TrxSalesReturnResponse cancelReturn(Long id, TrxSalesReturnCancelRequest request);
    public Page<TrxSalesReturnResponse> searchData(Pageable pageable, TrxSalesReturnSearchRequest request);

}
