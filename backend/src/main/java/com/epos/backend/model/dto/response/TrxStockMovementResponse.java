package com.epos.backend.model.dto.response;

import java.sql.Timestamp;

import com.epos.backend.enums.StockMovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrxStockMovementResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private StockMovementType movementType;
    private Long qtyBefore;
    private Long qtyChange;
    private Long qtyAfter;
    private String referenceNo;
    private String notes;
    private String createdBy;
    private Timestamp createdAt;

}
