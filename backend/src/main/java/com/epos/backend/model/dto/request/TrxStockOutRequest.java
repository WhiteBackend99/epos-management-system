package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrxStockOutRequest {

    @NotNull(message = "Produk wajib diisi")
    private Long productId;

    @NotNull(message = "Kuantiti wajib diisi")
    @Min(value = 1, message = "Kuantiti minimal harus 1")
    private Long qty;
    
    private String referenceNo;
    private String notes;

}
