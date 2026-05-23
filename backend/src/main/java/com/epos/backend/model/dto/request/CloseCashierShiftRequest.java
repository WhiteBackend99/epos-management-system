package com.epos.backend.model.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloseCashierShiftRequest {

    @NotNull(message = "Jumlah uang aktual wajib diisi")
    @DecimalMin(value = "0.00", message = "Jumlah uang aktual tidak boleh negatif")
    private BigDecimal actualCash;

    private String closeNotes;
}
