package com.epos.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.epos.backend.model.dto.request.ProductRequest;
import com.epos.backend.model.dto.response.ProductResponse;
import com.epos.backend.model.entity.Category;
import com.epos.backend.model.entity.Product;
import com.epos.backend.repository.CategoryRepository;
import com.epos.backend.repository.ProductRepository;
import com.epos.backend.service.ProductService;
import com.epos.backend.specification.ProductSpecification;
import com.epos.backend.util.GeneratorUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends Services implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

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
            .build();
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new RuntimeException("Produk dengan nama tersebut sudah ada");
        }
        
        Category dataCategory = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));
        Product newData = Product.builder()
            .category(dataCategory)
            .sku(GeneratorUtil.generateSku())
            .barcode(request.getBarcode())
            .name(request.getName())
            .description(request.getDescription())
            .purchasePrice(request.getPurchasePrice())
            .sellingPrice(request.getSellingPrice())
            .stock(request.getStock())
            .minStock(request.getMinStock())
            .isActive(request.getIsActive() != null ? request.getIsActive() : null)
            .isDeleted(false)
            .createdAt(now())
            .createdBy(getCurrentUsername())
            .build();

        productRepository.save(newData);
        return buildEntityToResponse(newData);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product data = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
        Category dataCategory = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        data.setCategory(dataCategory);
        data.setBarcode(request.getBarcode());
        data.setName(request.getName());
        data.setDescription(request.getDescription());
        data.setPurchasePrice(request.getPurchasePrice());
        data.setSellingPrice(request.getSellingPrice());
        data.setStock(request.getStock());
        data.setMinStock(request.getMinStock());
        data.setIsActive(request.getIsActive());
        data.setUpdatedAt(now());
        data.setUpdatedBy(getCurrentUsername());

        productRepository.save(data);

        return buildEntityToResponse(data);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product data = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Override
    public Page<ProductResponse> getAllProducts(int page, int size, String search, Boolean isActive) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Specification<Product> specification = Specification.where(ProductSpecification.notDeleted().and(ProductSpecification.search(search)).and(ProductSpecification.active(isActive)));
        return productRepository.findAll(specification, pageable).map(this::buildEntityToResponse);
    }

    @Override
    public void deleteProduct(Long id) {
        Product data = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
        data.setIsDeleted(true);
        data.setDeletedAt(now());
        data.setDeletedBy(getCurrentUsername());
        productRepository.save(data);
    }

}
