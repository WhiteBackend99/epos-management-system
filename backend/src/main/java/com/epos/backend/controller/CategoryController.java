package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.CategoryRequest;
import com.epos.backend.model.dto.request.search.CategorySearchRequest;
import com.epos.backend.model.dto.response.CategoryResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @AuditLog(type = AuditType.CATEGORY, action = AuditAction.CREATE)
    @PostMapping("/create-data")
    public ResponseEntity<ResponseData<CategoryResponse>> createData(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ResponseData.success("Kategori berhasil dibuat", categoryService.createData(request)));
    }

    @AuditLog(type = AuditType.CATEGORY, action = AuditAction.UPDATE, referenceIdField = "id")
    @PutMapping("/update-data/{id}")
    public ResponseEntity<ResponseData<CategoryResponse>> updateData(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ResponseData.success("Kategori berhasil diperbarui", categoryService.updateData(id, request)));
    }

    @AuditLog(type = AuditType.CATEGORY, action = AuditAction.VIEW, referenceIdField = "id")
    @GetMapping("/get-data/{id}")
    public ResponseEntity<ResponseData<CategoryResponse>> getDataById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Kategori berhasil ditemukan", categoryService.getDataById(id)));
    }

    @AuditLog(type = AuditType.CATEGORY, action = AuditAction.VIEW)
    @PostMapping("/search-data")
    public ResponseEntity<ResponseData<Page<CategoryResponse>>> searchData(@RequestBody CategorySearchRequest request) {
        return ResponseEntity.ok(ResponseData.success("Data kategori berhasil ditemukan", categoryService.searchData(request)));
    }

    @AuditLog(type = AuditType.CATEGORY, action = AuditAction.DELETE, referenceIdField = "id")
    @DeleteMapping("/delete-data/{id}")
    public ResponseEntity<ResponseData<CategoryResponse>> deleteData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Kategori berhasil dihapus", categoryService.deleteData(id)));
    }

    @AuditLog(type = AuditType.CATEGORY, action = AuditAction.UPDATE, referenceIdField = "id")
    @PatchMapping("/activate-data/{id}")
    public ResponseEntity<ResponseData<CategoryResponse>> activateData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Kategori berhasil diaktifkan", categoryService.activateData(id)));
    }

    @AuditLog(type = AuditType.CATEGORY, action = AuditAction.UPDATE, referenceIdField = "id")
    @PatchMapping("/deactivate-data/{id}")
    public ResponseEntity<ResponseData<CategoryResponse>> deactivateData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Kategori berhasil dinonaktifkan", categoryService.deactivateData(id)));
    }
    
}
