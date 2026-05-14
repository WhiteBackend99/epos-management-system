package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.TrxStockAdjustmentRequest;
import com.epos.backend.model.dto.request.TrxStockInRequest;
import com.epos.backend.model.dto.request.TrxStockOutRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.TrxStockMovementResponse;
import com.epos.backend.service.TrxStockMovementService;

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
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class TrxStockMovementController {

    private final TrxStockMovementService trxStockMovementService;

    @AuditLog(
        type = AuditType.STOCK_MOVEMENT,
        action = AuditAction.STOCK_IN,
        referenceIdField = "productId"
    )
    @PostMapping(value = "/stock-in")
    public ResponseEntity<ResponseData<TrxStockMovementResponse>> stockIn(@Valid @RequestBody TrxStockInRequest request) {
        ResponseData<TrxStockMovementResponse> response = ResponseData.<TrxStockMovementResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil menambahkan stok")
            .data(trxStockMovementService.stockIn(request))
            .build();
        return ResponseEntity.ok(response);
    }
    
    @AuditLog(
        type = AuditType.STOCK_MOVEMENT,
        action = AuditAction.STOCK_OUT,
        referenceIdField = "productId"
    )
    @PostMapping(value = "/stock-out")
    public ResponseEntity<ResponseData<TrxStockMovementResponse>> stockOut(@Valid @RequestBody TrxStockOutRequest request) {
        ResponseData<TrxStockMovementResponse> response = ResponseData.<TrxStockMovementResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mengurangi stok")
            .data(trxStockMovementService.stockOut(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.STOCK_MOVEMENT,
        action = AuditAction.STOCK_ADJUSTMENT,
        referenceIdField = "productId"
    )
    @PostMapping(value = "/adjustment")
    public ResponseEntity<ResponseData<TrxStockMovementResponse>> adjustment(@Valid @RequestBody TrxStockAdjustmentRequest request) {
        ResponseData<TrxStockMovementResponse> response = ResponseData.<TrxStockMovementResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mengatur ulang stok")
            .data(trxStockMovementService.adjustment(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.STOCK_MOVEMENT,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<TrxStockMovementResponse>> getDataById(@PathVariable Long id) {
        ResponseData<TrxStockMovementResponse> response = ResponseData.<TrxStockMovementResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mendapatkan data perpindahan stok")
            .data(trxStockMovementService.getDataById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.STOCK_MOVEMENT,
        action = AuditAction.VIEW
    )
    @GetMapping(value = "/search-data")
    public ResponseEntity<ResponseData<Page<TrxStockMovementResponse>>> searchData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
        ResponseData<Page<TrxStockMovementResponse>> response = ResponseData.<Page<TrxStockMovementResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil melakukan pencarian data perpindahan stok")
            .data(trxStockMovementService.searchData(page, size, search))
            .build();
        return ResponseEntity.ok(response);
    }

}
