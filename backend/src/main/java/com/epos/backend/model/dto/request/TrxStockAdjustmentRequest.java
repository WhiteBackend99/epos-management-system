package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrxStockAdjustmentRequest {

    @NotNull(message = "Produk wajib diisi")
    private Long productId;

    @NotNull(message = "Stok baru wajib diisi")
    @Min(value = 0, message = "Stok baru tidak boleh dibawah 0")
    private Long newStock;
    
    private String externalReferenceNo;
    private String notes;
    
}
