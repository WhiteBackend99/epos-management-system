package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.TrxPurchaseCancelRequest;
import com.epos.backend.model.dto.request.TrxPurchaseRequest;
import com.epos.backend.model.dto.request.search.TrxPurchaseSearchRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.TrxPurchaseResponse;
import com.epos.backend.service.TrxPurchaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class TrxPurchaseController {

    private final TrxPurchaseService trxPurchaseService;

    @AuditLog(
        type = AuditType.PURCHASE,
        action = AuditAction.CREATE
    )
    @PostMapping(value = "/create-data")
    public ResponseEntity<ResponseData<TrxPurchaseResponse>> createData(@Valid @RequestBody TrxPurchaseRequest request) {
        ResponseData<TrxPurchaseResponse> response = ResponseData.<TrxPurchaseResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil membuat pembelian")
            .data(trxPurchaseService.createPurchase(request))
            .build();
        return ResponseEntity.ok(response);
    }
    
    @AuditLog(
        type = AuditType.PURCHASE,
        action = AuditAction.UPDATE,
        referenceIdField = "id"
    )
    @PostMapping(value = "/update-data/{id}")
    public ResponseEntity<ResponseData<TrxPurchaseResponse>> updateData(@PathVariable Long id, @Valid @RequestBody TrxPurchaseRequest request) {
        ResponseData<TrxPurchaseResponse> response = ResponseData.<TrxPurchaseResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Pembelian berhasil diperbarui")
            .data(trxPurchaseService.updatePurchase(id, request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PURCHASE,
        action = AuditAction.COMPLETE,
        referenceIdField = "id"
    )
    @PostMapping(value = "/complete-data/{id}")
    public ResponseEntity<ResponseData<TrxPurchaseResponse>> completeData(@PathVariable Long id) {
        ResponseData<TrxPurchaseResponse> response = ResponseData.<TrxPurchaseResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Pembelian terselesaikan dan stok otomatis bertambah")
            .data(trxPurchaseService.completePurchase(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PURCHASE,
        action = AuditAction.CANCEL,
        referenceIdField = "id"
    )
    @PostMapping(value = "/cancel-data/{id}")
    public ResponseEntity<ResponseData<TrxPurchaseResponse>> cancelData(@PathVariable Long id, @Valid @RequestBody TrxPurchaseCancelRequest request) {
        ResponseData<TrxPurchaseResponse> response = ResponseData.<TrxPurchaseResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Pembelian telah dibatalkan")
            .data(trxPurchaseService.cancelPurchase(id, request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PURCHASE,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<TrxPurchaseResponse>> getDataById(@PathVariable Long id) {
        ResponseData<TrxPurchaseResponse> response = ResponseData.<TrxPurchaseResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mendapatkan data pembelian")
            .data(trxPurchaseService.getPurchaseById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PURCHASE,
        action = AuditAction.VIEW
    )
    @GetMapping(value = "/search-data")
    public ResponseEntity<ResponseData<Page<TrxPurchaseResponse>>> searchData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestBody(required = false) TrxPurchaseSearchRequest request) {
        ResponseData<Page<TrxPurchaseResponse>> response = ResponseData.<Page<TrxPurchaseResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil melakukan pencarian data pembelian")
            .data(trxPurchaseService.searchData(page, size, request))
            .build();
        return ResponseEntity.ok(response);
    }

}
