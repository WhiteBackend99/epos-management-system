package com.epos.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.epos.backend.model.dto.request.CategoryRequest;
import com.epos.backend.model.dto.response.CategoryResponse;
import com.epos.backend.model.entity.Category;
import com.epos.backend.repository.CategoryRepository;
import com.epos.backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends Services implements CategoryService {
    
    private final CategoryRepository categoryRepository;

    private CategoryResponse entityToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
    
    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Kategori dengan nama " + request.getName() + " sudah ada");
        }

        Category newData = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build();
        categoryRepository.save(newData);
        return entityToResponse(newData);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category data = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));
        return entityToResponse(data);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category data = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));
        data.setName(request.getName());
        data.setDescription(request.getDescription());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        categoryRepository.save(data);
        return entityToResponse(data);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::entityToResponse).toList();
    }

    @Override
    public void deleteCategory(Long id) {
        Category data = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan dengan id: " + id));
        categoryRepository.delete(data);
    }

}
