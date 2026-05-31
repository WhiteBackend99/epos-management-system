package com.epos.backend.model.dto.request.search;

import lombok.Data;

@Data
public class CategorySearchRequest {

    private String keyword;
    private String name;
    private Boolean isActive;

    private int page = 0;
    private int size = 10;
    
}
