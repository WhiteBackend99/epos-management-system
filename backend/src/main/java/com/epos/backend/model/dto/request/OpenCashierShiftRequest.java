package com.epos.backend.model.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenCashierShiftRequest {

    @NotNull(message = "Pembukaan keuangan kasir wajib diisi")
    @DecimalMin(value = "0.00", message = "Pembukaan keuangan kasir tidak boleh negatif")
    private BigDecimal openingCash;
    
    private String openNotes;

}
