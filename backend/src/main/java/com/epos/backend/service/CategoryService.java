package com.epos.backend.service;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.CategoryRequest;
import com.epos.backend.model.dto.request.search.CategorySearchRequest;
import com.epos.backend.model.dto.response.CategoryResponse;

public interface CategoryService {

    public CategoryResponse createData(CategoryRequest request);
    public CategoryResponse updateData(Long id, CategoryRequest request);
    public CategoryResponse getDataById(Long id);
    public CategoryResponse deleteData(Long id);
    public CategoryResponse activateData(Long id);
    public CategoryResponse deactivateData(Long id);
    public Page<CategoryResponse> searchData(CategorySearchRequest request);

}
