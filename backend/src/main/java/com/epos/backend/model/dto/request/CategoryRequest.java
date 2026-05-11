package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {

    @NotBlank(message = "Nama harus diisi")
    private String name;
    private String description;

}
