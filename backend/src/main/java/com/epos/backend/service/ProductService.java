package com.epos.backend.service;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.ProductRequest;
import com.epos.backend.model.dto.request.search.ProductSearchRequest;
import com.epos.backend.model.dto.response.ProductResponse;

public interface ProductService {

    public ProductResponse createProduct(ProductRequest request);
    public ProductResponse updateProduct(Long id, ProductRequest request);
    public ProductResponse getProductById(Long id);
    public ProductResponse deleteProduct(Long id);
    public ProductResponse activateProduct(Long id);
    public ProductResponse deactivateProduct(Long id);
    public Page<ProductResponse> searchData(ProductSearchRequest request);

}
