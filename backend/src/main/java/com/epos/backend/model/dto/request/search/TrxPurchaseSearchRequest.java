package com.epos.backend.model.dto.request.search;

import java.util.Date;

import com.epos.backend.enums.PurchaseStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrxPurchaseSearchRequest {

    private String purchaseNo;
    private String invoiceNo;
    private String supplierName;
    private String productName;
    private String sku;
    private PurchaseStatus status;
    private Date purchaseDateFrom;
    private Date purchaseDateTo;

}
