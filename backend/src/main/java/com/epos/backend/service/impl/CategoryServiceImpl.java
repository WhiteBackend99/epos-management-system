package com.epos.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.exception.ConflictException;
import com.epos.backend.exception.NotFoundException;
import com.epos.backend.model.dto.request.CategoryRequest;
import com.epos.backend.model.dto.request.search.CategorySearchRequest;
import com.epos.backend.model.dto.response.CategoryResponse;
import com.epos.backend.model.entity.Category;
import com.epos.backend.repository.CategoryRepository;
import com.epos.backend.service.CategoryService;
import com.epos.backend.specification.CategorySpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends Services implements CategoryService {
    
    private final CategoryRepository categoryRepository;

    private CategoryResponse buildEntitytoResponse(Category data) {
        return CategoryResponse.builder()
                .id(data.getId())
                .name(data.getName())
                .description(data.getDescription())
                .isActive(data.getIsActive())
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())
                .updatedBy(data.getUpdatedBy())
                .updatedAt(data.getUpdatedAt())
            .build();
    }

    @Transactional
    @Override
    public CategoryResponse createData(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCaseAndIsDeletedFalse(request.getName())) throw new ConflictException("Nama kategori sudah digunakan");

        Category data = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .isDeleted(false)
                .createdBy(getCurrentUsername())
                .createdAt(now())
            .build();

        return buildEntitytoResponse(categoryRepository.save(data));
    }

    @Transactional
    @Override
    public CategoryResponse updateData(Long id, CategoryRequest request) {
        Category data = categoryRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));

        if (categoryRepository.existsByNameIgnoreCaseAndIdNotAndIsDeletedFalse(request.getName(), id)) throw new ConflictException("Nama kategori sudah digunakan");

        data.setName(request.getName());
        data.setDescription(request.getDescription());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());

        return buildEntitytoResponse(categoryRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponse getDataById(Long id) {
        Category data = categoryRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));
        return buildEntitytoResponse(data);
    }

    @Transactional
    @Override
    public CategoryResponse deleteData(Long id) {
        Category data = categoryRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));
        data.setIsDeleted(true);
        data.setIsActive(false);
        data.setDeletedBy(getCurrentUsername());
        data.setDeletedAt(now());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntitytoResponse(categoryRepository.save(data));
    }

    @Transactional
    @Override
    public CategoryResponse activateData(Long id) {
        Category data = categoryRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));
        data.setIsActive(true);
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntitytoResponse(categoryRepository.save(data));
    }

    @Transactional
    @Override
    public CategoryResponse deactivateData(Long id) {
        Category data = categoryRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));
        data.setIsActive(false);
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntitytoResponse(categoryRepository.save(data));
    }

    @Override
    public Page<CategoryResponse> searchData(CategorySearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return categoryRepository.findAll(CategorySpecification.search(request), pageable).map(this::buildEntitytoResponse);
    }}
