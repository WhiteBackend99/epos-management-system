package com.epos.backend.model.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "Kategori wajib diisi")
    private Long categoryId;

    private String barcode;
    
    @NotBlank(message = "Nama Produk wajib diisi")
    @Size(max = 255)
    private String name;

    private String description;

    @NotNull(message = "Harga Beli wajib diisi")
    @DecimalMin(value = "0")
    private BigDecimal purchasePrice;

    @NotNull(message = "Harga Jual wajib diisi")
    @DecimalMin(value = "0")
    private BigDecimal sellingPrice;

    @NotNull(message = "Stok wajib diisi")
    @Min(value = 0)
    private Integer stock;

    @NotNull(message = "Minimum Stok wajib diisi")
    @Min(value = 0)
    private Integer minStock;
    
    private Boolean isActive;

}
