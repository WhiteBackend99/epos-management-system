package com.epos.backend.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.CloseCashierShiftRequest;
import com.epos.backend.model.dto.request.OpenCashierShiftRequest;
import com.epos.backend.model.dto.request.search.TrxCashierShiftSearchRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.TrxCashierShiftResponse;
import com.epos.backend.service.TrxCashierShiftService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cashier-shifts")
@RequiredArgsConstructor
public class TrxCashierShiftController {

    private final TrxCashierShiftService trxCashierShiftService;
    
    @AuditLog(
        type = AuditType.CASHIER_SHIFT,
        action = AuditAction.OPEN
    )
    @PostMapping(value = "/open")
    public ResponseEntity<ResponseData<TrxCashierShiftResponse>> openShift(@Valid @RequestBody OpenCashierShiftRequest request) {
        ResponseData<TrxCashierShiftResponse> response = ResponseData.<TrxCashierShiftResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil membuka shift kasir")
            .data(trxCashierShiftService.openShift(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CASHIER_SHIFT,
        action = AuditAction.CLOSE,
        referenceIdField = "id"
    )
    @PutMapping(value = "/close")
    public ResponseEntity<ResponseData<TrxCashierShiftResponse>> closeShift(@Valid @RequestBody CloseCashierShiftRequest request) {
        ResponseData<TrxCashierShiftResponse> response = ResponseData.<TrxCashierShiftResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil menutup shift kasir")
            .data(trxCashierShiftService.closeShift(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CASHIER_SHIFT,
        action = AuditAction.CURRENT_OPEN_SHIFT,
        referenceIdField = "id"
    )
    @GetMapping(value = "/current-open-shift")
    public ResponseEntity<ResponseData<TrxCashierShiftResponse>> getCurrentOpenShift() {
        ResponseData<TrxCashierShiftResponse> response = ResponseData.<TrxCashierShiftResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Berhasil mengambil shift kasir yang sedang dibuka")
            .data(trxCashierShiftService.getCurrentOpenShift())
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CASHIER_SHIFT,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<TrxCashierShiftResponse>> getDataById(@PathVariable Long id) {
        ResponseData<TrxCashierShiftResponse> response = ResponseData.<TrxCashierShiftResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Data shift kasir berhasil ditemukan")
            .data(trxCashierShiftService.getDataById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CASHIER_SHIFT,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data-number/{shiftNo}")
    public ResponseEntity<ResponseData<TrxCashierShiftResponse>> getDataByShiftNo(@PathVariable String shiftNo) {
        ResponseData<TrxCashierShiftResponse> response = ResponseData.<TrxCashierShiftResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Data shift kasir berhasil ditemukan")
            .data(trxCashierShiftService.getByShiftNo(shiftNo))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CASHIER_SHIFT,
        action = AuditAction.VIEW
    )
    @GetMapping(value = "/search-data")
    public ResponseEntity<ResponseData<Page<TrxCashierShiftResponse>>> searchData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestBody(required = false) TrxCashierShiftSearchRequest request) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ResponseData<Page<TrxCashierShiftResponse>> response = ResponseData.<Page<TrxCashierShiftResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Data shift kasir berhasil ditemukan")
            .data(trxCashierShiftService.searchData(pageable, request))
            .build();
        return ResponseEntity.ok(response);
    }

}
