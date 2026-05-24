package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReprintReceiptRequest {

    @NotBlank(message = "No Receipt wajib diisi")
    private String receiptNo;

    private String notes;
}
