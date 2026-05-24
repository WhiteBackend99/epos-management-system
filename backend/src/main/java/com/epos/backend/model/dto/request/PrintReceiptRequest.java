package com.epos.backend.model.dto.request;

import com.epos.backend.enums.ReceiptReferenceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrintReceiptRequest {

    @NotNull(message = "Tipe referensi wajib diisi")
    private ReceiptReferenceType referenceType;

    @NotBlank(message = "No referensi wajib diisi")
    private String referenceNo;

    private String notes;

}
