package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.SupplierRequest;
import com.epos.backend.model.dto.request.search.SupplierSearchRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.SupplierResponse;
import com.epos.backend.service.SupplierService;

import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @AuditLog(
        type = AuditType.SUPPLIER,
        action = AuditAction.CREATE
    )
    @PostMapping(value = "/create-data")
    public ResponseEntity<ResponseData<SupplierResponse>> createData(@Valid @RequestBody SupplierRequest request, HttpServletRequest servletRequest) {
        ResponseData<SupplierResponse> response = ResponseData.<SupplierResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil membuat supplier")
            .data(supplierService.createSupplier(request))
            .build();
        return ResponseEntity.ok(response);
    }
    
    @AuditLog(
        type = AuditType.SUPPLIER,
        action = AuditAction.UPDATE,
        referenceIdField = "id"
    )
    @PostMapping(value = "/update-data/{id}")
    public ResponseEntity<ResponseData<SupplierResponse>> updateData(@PathVariable Long id, @Valid @RequestBody SupplierRequest request, HttpServletRequest servletRequest) {
        ResponseData<SupplierResponse> response = ResponseData.<SupplierResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Supplier berhasil diperbarui")
            .data(supplierService.updateSupplier(id, request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.SUPPLIER,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<SupplierResponse>> getDataById(@PathVariable Long id, HttpServletRequest servletRequest) {
        ResponseData<SupplierResponse> response = ResponseData.<SupplierResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mendapatkan supplier")
            .data(supplierService.getSupplierById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.SUPPLIER,
        action = AuditAction.VIEW
    )
    @GetMapping(value = "/search-data")
    public ResponseEntity<ResponseData<Page<SupplierResponse>>> searchData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestBody(required = false) SupplierSearchRequest request, HttpServletRequest servletRequest) {
        ResponseData<Page<SupplierResponse>> response = ResponseData.<Page<SupplierResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil melakukan pencarian data supplier")
            .data(supplierService.searchData(page, size, request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.SUPPLIER,
        action = AuditAction.DELETE,
        referenceIdField = "id"
    )
    @DeleteMapping(value = "/delete-data/{id}")
    public ResponseEntity<ResponseData<Object>> deleteData(@PathVariable Long id) {
        ResponseData<Object> response = ResponseData.<Object> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil menghapus supplier")
            .data(null)
            .build();
        return ResponseEntity.ok(response);
    }

}
