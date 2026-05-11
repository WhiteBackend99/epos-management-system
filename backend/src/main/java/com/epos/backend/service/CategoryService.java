package com.epos.backend.service;

import java.util.List;

import com.epos.backend.model.dto.request.CategoryRequest;
import com.epos.backend.model.dto.response.CategoryResponse;

public interface CategoryService {

    public CategoryResponse createCategory(CategoryRequest request);
    public CategoryResponse getCategoryById(Long id);
    public CategoryResponse updateCategory(Long id, CategoryRequest request);
    public List<CategoryResponse> getAllCategories();
    public void deleteCategory(Long id);

}
