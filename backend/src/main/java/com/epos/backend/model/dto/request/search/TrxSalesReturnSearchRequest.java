package com.epos.backend.model.dto.request.search;

import java.util.Date;

import com.epos.backend.enums.RefundStatus;
import com.epos.backend.enums.SalesReturnStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrxSalesReturnSearchRequest {

    private String keyword;
    private SalesReturnStatus status;
    private RefundStatus refundStatus;
    private Date startDate;
    private Date endDate;

}
