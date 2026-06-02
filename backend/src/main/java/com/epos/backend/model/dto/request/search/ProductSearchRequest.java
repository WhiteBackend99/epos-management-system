package com.epos.backend.model.dto.request.search;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    private String keyword;
    private String sku;
    private String barcode;
    private String name;
    private Long categoryId;
    private Boolean isActive;
    private BigDecimal minSellingPrice;
    private BigDecimal maxSellingPrice;
    private Long minStock;
    private Long maxStock;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

}
