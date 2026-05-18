package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PosSalesCancelRequest {

    @NotBlank(message = "Alasan pembatalan wajib diisi")
    private String cancelReason;

}
