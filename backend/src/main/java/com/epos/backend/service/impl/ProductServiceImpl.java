package com.epos.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.epos.backend.exception.ConflictException;
import com.epos.backend.exception.NotFoundException;
import com.epos.backend.model.dto.request.ProductRequest;
import com.epos.backend.model.dto.request.search.ProductSearchRequest;
import com.epos.backend.model.dto.response.ProductResponse;
import com.epos.backend.model.entity.Category;
import com.epos.backend.model.entity.Product;
import com.epos.backend.repository.CategoryRepository;
import com.epos.backend.repository.ProductRepository;
import com.epos.backend.service.ProductService;
import com.epos.backend.specification.ProductSpecification;
import com.epos.backend.util.ParseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends Services implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private void validateCreate(ProductRequest request) {
        if (productRepository.existsByNameIgnoreCaseAndIsDeletedFalse(request.getName().trim())) {
            throw new ConflictException("Produk dengan nama tersebut sudah ada");
        }

        if (StringUtils.hasText(request.getBarcode()) && productRepository.existsByBarcodeIgnoreCaseAndIsDeletedFalse(request.getBarcode().trim())) {
            throw new ConflictException("Barcode produk sudah digunakan");
        }
    }

    private void validateUpdate(Long id, ProductRequest request) {
        if (productRepository.existsByNameIgnoreCaseAndIdNotAndIsDeletedFalse(request.getName().trim(), id)) {
            throw new ConflictException("Produk dengan nama tersebut sudah ada");
        }

        if (StringUtils.hasText(request.getBarcode()) && productRepository.existsByBarcodeIgnoreCaseAndIdNotAndIsDeletedFalse(request.getBarcode().trim(), id)) {
            throw new ConflictException("Barcode produk sudah digunakan");
        }
    }

    private ProductResponse buildEntityToResponse(Product data) {
        return ProductResponse.builder()
                .id(data.getId())
                .categoryId(data.getCategory().getId())
                .categoryName(data.getCategory().getName())
                .sku(data.getSku())
                .barcode(data.getBarcode())
                .name(data.getName())
                .description(data.getDescription())
                .purchasePrice(data.getPurchasePrice())
                .sellingPrice(data.getSellingPrice())
                .stock(data.getStock())
                .minStock(data.getMinStock())
                .isActive(data.getIsActive())
                .createdAt(data.getCreatedAt())
                .createdBy(data.getCreatedBy())
                .updatedAt(data.getUpdatedAt())
                .updatedBy(data.getUpdatedBy())
            .build();
    }

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        validateCreate(request);

        Category dataCategory = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId()).orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));

        Product newData = Product.builder()
                .category(dataCategory)
                .sku(automationGeneratorServices.generateProductCode())
                .barcode(ParseUtil.trimToNull(request.getBarcode()))
                .name(request.getName().trim())
                .description(ParseUtil.trimToNull(request.getDescription()))
                .purchasePrice(request.getPurchasePrice())
                .sellingPrice(request.getSellingPrice())
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .isActive(request.getIsActive() == null ? true : request.getIsActive())
                .isDeleted(false)
                .createdAt(now())
                .createdBy(getCurrentUsername())
            .build();

        return buildEntityToResponse(productRepository.save(newData));
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product data = productRepository.findActiveProductForUpdate(id).orElseThrow(() -> new NotFoundException("Produk tidak ditemukan"));

        validateUpdate(id, request);

        Category dataCategory = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId()).orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));

        data.setCategory(dataCategory);
        data.setBarcode(ParseUtil.trimToNull(request.getBarcode()));
        data.setName(request.getName().trim());
        data.setDescription(ParseUtil.trimToNull(request.getDescription()));
        data.setPurchasePrice(request.getPurchasePrice());
        data.setSellingPrice(request.getSellingPrice());
        data.setStock(request.getStock());
        data.setMinStock(request.getMinStock());
        data.setIsActive(request.getIsActive() == null ? data.getIsActive() : request.getIsActive());
        data.setUpdatedAt(now());
        data.setUpdatedBy(getCurrentUsername());

        return buildEntityToResponse(productRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(Long id) {
        Product data = productRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NotFoundException("Produk tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional
    @Override
    public ProductResponse deleteProduct(Long id) {
        Product data = productRepository.findActiveProductForUpdate(id).orElseThrow(() -> new NotFoundException("Produk tidak ditemukan"));
        data.setIsDeleted(true);
        data.setIsActive(false);
        data.setDeletedAt(now());
        data.setDeletedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        data.setUpdatedBy(getCurrentUsername());
        return buildEntityToResponse(productRepository.save(data));
    }

    @Transactional
    @Override
    public ProductResponse activateProduct(Long id) {
        Product data = productRepository.findActiveProductForUpdate(id).orElseThrow(() -> new NotFoundException("Produk tidak ditemukan"));
        data.setIsActive(true);
        data.setUpdatedAt(now());
        data.setUpdatedBy(getCurrentUsername());
        return buildEntityToResponse(productRepository.save(data));
    }

    @Transactional
    @Override
    public ProductResponse deactivateProduct(Long id) {
        Product data = productRepository.findActiveProductForUpdate(id).orElseThrow(() -> new NotFoundException("Produk tidak ditemukan"));
        data.setIsActive(false);
        data.setUpdatedAt(now());
        data.setUpdatedBy(getCurrentUsername());
        return buildEntityToResponse(productRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> searchData(ProductSearchRequest request) {
        ProductSearchRequest searchRequest = request == null ? new ProductSearchRequest() : request;
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        return productRepository.findAll(ProductSpecification.search(searchRequest), pageable).map(this::buildEntityToResponse);
    }
    
}
