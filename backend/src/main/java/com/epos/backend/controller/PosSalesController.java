package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.PosSalesCancelRequest;
import com.epos.backend.model.dto.request.PosSalesRequest;
import com.epos.backend.model.dto.response.PosSalesResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.PosSalesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/pos-sales")
@RequiredArgsConstructor
public class PosSalesController {

    private final PosSalesService posSalesService;

    @AuditLog(
        type = AuditType.POS_SALES,
        action = AuditAction.CREATE
    )
    @PostMapping(value = "/create-data")
    public ResponseEntity<ResponseData<PosSalesResponse>> createData(@Valid @RequestBody PosSalesRequest request) {
        ResponseData<PosSalesResponse> response = ResponseData.<PosSalesResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Transaksi penjualan berhasil dibuat")
            .data(posSalesService.createSales(request))
            .build();
        return ResponseEntity.ok(response);
    }
    
    @AuditLog(
        type = AuditType.POS_SALES,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<PosSalesResponse>> getDataById(@PathVariable Long id) {
        ResponseData<PosSalesResponse> response = ResponseData.<PosSalesResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mendapatkan data penjualan")
            .data(posSalesService.getSalesById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.POS_SALES,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data-number/{salesNo}")
    public ResponseEntity<ResponseData<PosSalesResponse>> getDataBySalesNo(@PathVariable String salesNo) {
        ResponseData<PosSalesResponse> response = ResponseData.<PosSalesResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mendapatkan data penjualan")
            .data(posSalesService.getSalesByNo(salesNo))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.POS_SALES,
        action = AuditAction.VIEW
    )
    @GetMapping(value = "/search-data")
    public ResponseEntity<ResponseData<Page<PosSalesResponse>>> searchData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String keyword) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        ResponseData<Page<PosSalesResponse>> response = ResponseData.<Page<PosSalesResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mendapatkan data penjualan")
            .data(posSalesService.searchData(keyword, pageable))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.POS_SALES,
        action = AuditAction.CANCEL
    )
    @PutMapping(value = "/cancel-data/{id}")
    public ResponseEntity<ResponseData<PosSalesResponse>> cancelData(@PathVariable Long id, @Valid @RequestBody PosSalesCancelRequest request) {
        ResponseData<PosSalesResponse> response = ResponseData.<PosSalesResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Transaksi penjualan berhasil dibatalkan")
            .data(posSalesService.cancelSales(id, request))
            .build();
        return ResponseEntity.ok(response);
    }

}
