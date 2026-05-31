package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemParameterRequest {

    @NotBlank(message = "Kode parameter wajib diisi")
    private String parameterCode;

    @NotBlank(message = "Nama parameter wajib diisi")
    private String parameterName;

    @NotBlank(message = "Nilai parameter wajib diisi")
    private String parameterValue;

    @NotBlank(message = "Tipe parameter wajib diisi")
    private String parameterType;

    private String description;
    private Boolean isActive;
    
}
