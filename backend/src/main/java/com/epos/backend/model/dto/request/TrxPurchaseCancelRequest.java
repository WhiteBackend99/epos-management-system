package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrxPurchaseCancelRequest {

    @NotBlank(message = "Alasan pembatalan wajib diisi")
    private String cancelReason;

}
