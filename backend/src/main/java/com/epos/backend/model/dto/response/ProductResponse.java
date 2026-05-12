package com.epos.backend.model.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String sku;
    private String barcode;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer stock;
    private Integer minStock;
    private Boolean isActive;
    private Timestamp createdAt;
    private String createdBy;

}
