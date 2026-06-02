package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.SupplierRequest;
import com.epos.backend.model.dto.request.search.SupplierSearchRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.SupplierResponse;
import com.epos.backend.service.SupplierService;

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
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @AuditLog(type = AuditType.SUPPLIER, action = AuditAction.CREATE)
    @PostMapping("/create-data")
    public ResponseEntity<ResponseData<SupplierResponse>> createData(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(ResponseData.success("Supplier berhasil dibuat", supplierService.createSupplier(request)));
    }

    @AuditLog(type = AuditType.SUPPLIER, action = AuditAction.UPDATE, referenceIdField = "id")
    @PutMapping("/update-data/{id}")
    public ResponseEntity<ResponseData<SupplierResponse>> updateData(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(ResponseData.success("Supplier berhasil diperbarui", supplierService.updateSupplier(id, request)));
    }

    @AuditLog(type = AuditType.SUPPLIER, action = AuditAction.VIEW, referenceIdField = "id")
    @GetMapping("/get-data/{id}")
    public ResponseEntity<ResponseData<SupplierResponse>> getDataById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Supplier berhasil ditemukan", supplierService.getSupplierById(id)));
    }

    @AuditLog(type = AuditType.SUPPLIER, action = AuditAction.VIEW)
    @PostMapping("/search-data")
    public ResponseEntity<ResponseData<Page<SupplierResponse>>> searchData(@RequestBody(required = false) SupplierSearchRequest request) {
        return ResponseEntity.ok(ResponseData.success("Data supplier berhasil ditemukan", supplierService.searchData(request)));
    }

    @AuditLog(type = AuditType.SUPPLIER, action = AuditAction.DELETE, referenceIdField = "id")
    @DeleteMapping("/delete-data/{id}")
    public ResponseEntity<ResponseData<SupplierResponse>> deleteData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Supplier berhasil dihapus", supplierService.deleteSupplier(id)));
    }

    @AuditLog(type = AuditType.SUPPLIER, action = AuditAction.ACTIVE, referenceIdField = "id")
    @PatchMapping("/activate-data/{id}")
    public ResponseEntity<ResponseData<SupplierResponse>> activateData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Supplier berhasil diaktifkan", supplierService.activateSupplier(id)));
    }

    @AuditLog(type = AuditType.SUPPLIER, action = AuditAction.DEACTIVE, referenceIdField = "id")
    @PatchMapping("/deactivate-data/{id}")
    public ResponseEntity<ResponseData<SupplierResponse>> deactivateData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Supplier berhasil dinonaktifkan", supplierService.deactivateSupplier(id)));
    }

}