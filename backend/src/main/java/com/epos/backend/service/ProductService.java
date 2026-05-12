package com.epos.backend.service;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.ProductRequest;
import com.epos.backend.model.dto.response.ProductResponse;

public interface ProductService {

    public ProductResponse createProduct(ProductRequest request);
    public ProductResponse updateProduct(Long id, ProductRequest request);
    public ProductResponse getProductById(Long id);
    public Page<ProductResponse> getAllProducts(int page, int size, String search, Boolean isActive);
    public void deleteProduct(Long id);

}
