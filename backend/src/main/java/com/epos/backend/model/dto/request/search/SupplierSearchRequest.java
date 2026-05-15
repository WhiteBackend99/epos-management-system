package com.epos.backend.model.dto.request.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSearchRequest {

    private String supplierCode;
    private String name;
    private String phone;
    private String email;
    private String picName;
    private Boolean isActive;

}
