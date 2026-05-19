package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrxSalesReturnCancelRequest {

    @NotBlank(message = "Alasan return dibatalkan wajib diisi")
    private String cancelReason;
}
