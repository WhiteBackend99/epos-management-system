package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.CategoryRequest;
import com.epos.backend.model.dto.response.CategoryResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @AuditLog(
        type = AuditType.CATEGORY,
        action = AuditAction.CREATE
    )
    @PostMapping(value = "/create-data")
    public ResponseEntity<ResponseData<CategoryResponse>> createData(@Valid @RequestBody CategoryRequest request, HttpServletRequest servletRequest) {
        ResponseData<CategoryResponse> response = ResponseData.<CategoryResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil membuat kategori")
            .data(categoryService.createCategory(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CATEGORY,
        action = AuditAction.UPDATE,
        referenceIdField = "id"
    )
    @PostMapping(value = "/update-data/{id}")
    public ResponseEntity<ResponseData<CategoryResponse>> updateDataById(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        ResponseData<CategoryResponse> response = ResponseData.<CategoryResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Kategori berhasil diperbarui")
                .data(categoryService.updateCategory(id, request))
                .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CATEGORY,
        action = AuditAction.GET,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<CategoryResponse>> getDataById(@PathVariable Long id) {
        ResponseData<CategoryResponse> response = ResponseData.<CategoryResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Kategori berhasil ditemukan")
                .data(categoryService.getCategoryById(id))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/get-all-data")
    public ResponseEntity<ResponseData<List<CategoryResponse>>> getAllData() {
        ResponseData<List<CategoryResponse>> response = ResponseData.<List<CategoryResponse>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Kategori berhasil ditemukan")
                .data(categoryService.getAllCategories())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @AuditLog(
        type = AuditType.CATEGORY,
        action = AuditAction.DELETE
    )
    @DeleteMapping(value = "/delete-data/{id}")
    public ResponseEntity<ResponseData<Object>> deleteData(@PathVariable Long id) {
        categoryService.deleteCategory(id);

        ResponseData<Object> response = ResponseData.<Object> builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Kategori berhasil dihapus")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
    
}
