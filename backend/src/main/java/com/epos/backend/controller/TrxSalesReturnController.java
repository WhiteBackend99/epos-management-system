package com.epos.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.TrxSalesReturnCancelRequest;
import com.epos.backend.model.dto.request.TrxSalesReturnRequest;
import com.epos.backend.model.dto.request.search.TrxSalesReturnSearchRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.TrxSalesReturnResponse;
import com.epos.backend.service.TrxSalesReturnService;

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
@RequestMapping("/api/sales-returns")
@RequiredArgsConstructor
public class TrxSalesReturnController {

    private final TrxSalesReturnService trxSalesReturnService;

    @AuditLog(
        type = AuditType.SALES_RETURN,
        action = AuditAction.CREATE
    )
    @PostMapping(value = "/create-date")
    public ResponseEntity<ResponseData<TrxSalesReturnResponse>> createData(@Valid @RequestBody TrxSalesReturnRequest request) {
        ResponseData<TrxSalesReturnResponse> response = ResponseData.<TrxSalesReturnResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Pengembalian penjualan berhasil dibuat")
            .data(trxSalesReturnService.createReturn(request))
            .build();
        return ResponseEntity.ok(response);
    }
    
    @AuditLog(
        type = AuditType.SALES_RETURN,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<TrxSalesReturnResponse>> getDataById(@PathVariable Long id) {
        ResponseData<TrxSalesReturnResponse> response = ResponseData.<TrxSalesReturnResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Detail pengembalian penjualan berhasil ditemukan")
            .data(trxSalesReturnService.getDataById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.SALES_RETURN,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data-number/{returnNo}")
    public ResponseEntity<ResponseData<TrxSalesReturnResponse>> getDataByReturnNo(@PathVariable String returnNo) {
        ResponseData<TrxSalesReturnResponse> response = ResponseData.<TrxSalesReturnResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Detail pengembalian penjualan berhasil ditemukan")
            .data(trxSalesReturnService.getByReturnNo(returnNo))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.SALES_RETURN,
        action = AuditAction.VIEW
    )
    @GetMapping(value = "/search-data")
    public ResponseEntity<ResponseData<Page<TrxSalesReturnResponse>>> getDataByReturnNo(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestBody(required = false) TrxSalesReturnSearchRequest request) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        ResponseData<Page<TrxSalesReturnResponse>> response = ResponseData.<Page<TrxSalesReturnResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Data pengembalian penjualan berhasil ditemukan")
            .data(trxSalesReturnService.searchData(pageable, request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.SALES_RETURN,
        action = AuditAction.CANCEL,
        referenceIdField = "id"
    )
    @PutMapping(value = "/cancel-data/{id}")
    public ResponseEntity<ResponseData<TrxSalesReturnResponse>> cancelData(@PathVariable Long id, @Valid @RequestBody TrxSalesReturnCancelRequest request) {
        ResponseData<TrxSalesReturnResponse> response = ResponseData.<TrxSalesReturnResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Pengembalian penjualan berhasil dibatalkan")
            .data(trxSalesReturnService.cancelReturn(id, request))
            .build();
        return ResponseEntity.ok(response);
    }

}
