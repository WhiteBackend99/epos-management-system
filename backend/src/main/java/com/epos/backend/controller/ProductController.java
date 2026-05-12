package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.ProductRequest;
import com.epos.backend.model.dto.response.ProductResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @AuditLog(
        type = AuditType.PRODUCT,
        action = AuditAction.CREATE
    )
    @PostMapping(value = "/create-data")
    public ResponseEntity<ResponseData<ProductResponse>> createData(@Valid @RequestBody ProductRequest request) {
        ResponseData<ProductResponse> response = ResponseData.<ProductResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil membuat produk")
            .data(productService.createProduct(request))
            .build();
        return ResponseEntity.ok(response);
    }
    
    @AuditLog(
        type = AuditType.PRODUCT,
        action = AuditAction.UPDATE,
        referenceIdField = "id"
    )
    @PostMapping(value = "/update-data/{id}")
    public ResponseEntity<ResponseData<ProductResponse>> updateDataById(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        ResponseData<ProductResponse> response = ResponseData.<ProductResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Kategori berhasil diperbarui")
                .data(productService.updateProduct(id, request))
                .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PRODUCT,
        action = AuditAction.GET,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<ProductResponse>> getDataById(@PathVariable Long id) {
        ResponseData<ProductResponse> response = ResponseData.<ProductResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Kategori berhasil ditemukan")
                .data(productService.getProductById(id))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/search-data")
    public ResponseEntity<ResponseData<Page<ProductResponse>>> searchData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search, @RequestParam(required = false) Boolean isActive) {
        ResponseData<Page<ProductResponse>> response = ResponseData.<Page<ProductResponse>> builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Berhasil melakukan pencarian data produk")
                .data(productService.getAllProducts(page, size, search, isActive))
                .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PRODUCT,
        action = AuditAction.DELETE
    )
    @DeleteMapping(value = "/delete-data/{id}")
    public ResponseEntity<ResponseData<Object>> deleteData(@PathVariable Long id) {
        productService.deleteProduct(id);

        ResponseData<Object> response = ResponseData.<Object> builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Produk berhasil dihapus")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

}
