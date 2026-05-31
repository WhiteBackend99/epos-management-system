package com.epos.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.SystemParameterRequest;
import com.epos.backend.model.dto.request.search.SystemParameterSearchRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.SystemParameterResponse;
import com.epos.backend.service.SystemParameterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/system-parameters")
@RequiredArgsConstructor
public class SystemParameterController {

    private final SystemParameterService systemParameterService;

    @AuditLog(type = AuditType.SYSTEM_PARAMETER, action = AuditAction.CREATE)
    @PostMapping("/create-data")
    public ResponseEntity<ResponseData<SystemParameterResponse>> createData(@Valid @RequestBody SystemParameterRequest request) {
        return ResponseEntity.ok(ResponseData.success("Parameter berhasil dibuat", systemParameterService.createData(request)));
    }

    @AuditLog(type = AuditType.SYSTEM_PARAMETER, action = AuditAction.UPDATE, referenceIdField = "id")
    @PutMapping("/update-data/{id}")
    public ResponseEntity<ResponseData<SystemParameterResponse>> updateData(@PathVariable Long id, @Valid @RequestBody SystemParameterRequest request) {
        return ResponseEntity.ok(ResponseData.success("Parameter berhasil diperbarui", systemParameterService.updateData(id, request)));
    }

    @AuditLog(type = AuditType.SYSTEM_PARAMETER, action = AuditAction.VIEW, referenceIdField = "id")
    @GetMapping("/get-data/{id}")
    public ResponseEntity<ResponseData<SystemParameterResponse>> getDataById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Parameter berhasil ditemukan", systemParameterService.getDataById(id)));
    }

    @AuditLog(type = AuditType.SYSTEM_PARAMETER, action = AuditAction.VIEW, referenceIdField = "id")
    @GetMapping("/get-data-code/{parameterCode}")
    public ResponseEntity<ResponseData<SystemParameterResponse>> getDataByCode(@PathVariable String parameterCode) {
        return ResponseEntity.ok(ResponseData.success("Parameter berhasil ditemukan", systemParameterService.getDataByCode(parameterCode)));
    }

    @AuditLog(type = AuditType.SYSTEM_PARAMETER, action = AuditAction.VIEW)
    @PostMapping("/search-data")
    public ResponseEntity<ResponseData<Page<SystemParameterResponse>>> searchData(@RequestBody SystemParameterSearchRequest request) {
        return ResponseEntity.ok(ResponseData.success("Data parameter berhasil ditemukan", systemParameterService.searchData(request)));
    }

    @AuditLog(type = AuditType.SYSTEM_PARAMETER, action = AuditAction.DELETE, referenceIdField = "id")
    @DeleteMapping("/delete-data/{id}")
    public ResponseEntity<ResponseData<SystemParameterResponse>> deleteData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Parameter berhasil dihapus", systemParameterService.deleteData(id)));
    }

}
