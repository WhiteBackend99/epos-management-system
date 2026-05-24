package com.epos.backend.service;

import com.epos.backend.model.dto.request.PrintReceiptRequest;
import com.epos.backend.model.dto.request.ReprintReceiptRequest;
import com.epos.backend.model.dto.response.TrxReceiptResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface TrxReceiptService {

    public TrxReceiptResponse printReceipt(PrintReceiptRequest request, HttpServletRequest servletRequest);
    public TrxReceiptResponse reprintReceipt(ReprintReceiptRequest request, HttpServletRequest servletRequest);
    public TrxReceiptResponse getByReceiptNo(String receiptNo);

}
