package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.ProductRequest;
import com.epos.backend.model.dto.request.search.ProductSearchRequest;
import com.epos.backend.model.dto.response.ProductResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @AuditLog(type = AuditType.PRODUCT, action = AuditAction.CREATE)
    @PostMapping("/create-data")
    public ResponseEntity<ResponseData<ProductResponse>> createData(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ResponseData.success("Produk berhasil dibuat", productService.createProduct(request)));
    }

    @AuditLog(type = AuditType.PRODUCT, action = AuditAction.UPDATE, referenceIdField = "id")
    @PutMapping("/update-data/{id}")
    public ResponseEntity<ResponseData<ProductResponse>> updateDataById(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ResponseData.success("Produk berhasil diperbarui", productService.updateProduct(id, request)));
    }

    @AuditLog(type = AuditType.PRODUCT, action = AuditAction.VIEW, referenceIdField = "id")
    @GetMapping("/get-data/{id}")
    public ResponseEntity<ResponseData<ProductResponse>> getDataById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Produk berhasil ditemukan", productService.getProductById(id)));
    }

    @AuditLog(type = AuditType.PRODUCT, action = AuditAction.VIEW)
    @PostMapping("/search-data")
    public ResponseEntity<ResponseData<Page<ProductResponse>>> searchData(@RequestBody(required = false) ProductSearchRequest request) {
        return ResponseEntity.ok(ResponseData.success("Data produk berhasil ditemukan", productService.searchData(request)));
    }

    @AuditLog(type = AuditType.PRODUCT, action = AuditAction.DELETE, referenceIdField = "id")
    @DeleteMapping("/delete-data/{id}")
    public ResponseEntity<ResponseData<ProductResponse>> deleteData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Produk berhasil dihapus", productService.deleteProduct(id)));
    }

    @AuditLog(type = AuditType.PRODUCT, action = AuditAction.ACTIVE, referenceIdField = "id")
    @PatchMapping("/activate-data/{id}")
    public ResponseEntity<ResponseData<ProductResponse>> activateData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Produk berhasil diaktifkan", productService.activateProduct(id)));
    }

    @AuditLog(type = AuditType.PRODUCT, action = AuditAction.DEACTIVE, referenceIdField = "id")
    @PatchMapping("/deactivate-data/{id}")
    public ResponseEntity<ResponseData<ProductResponse>> deactivateData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Produk berhasil dinonaktifkan", productService.deactivateProduct(id)));
    }

}
