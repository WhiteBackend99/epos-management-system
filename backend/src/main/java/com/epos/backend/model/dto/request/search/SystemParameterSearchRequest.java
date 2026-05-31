package com.epos.backend.model.dto.request.search;

import lombok.Data;

@Data
public class SystemParameterSearchRequest {

    private String keyword;
    private String parameterCode;
    private String parameterType;
    private Boolean isActive;

    private int page = 0;
    private int size = 10;
    
}
