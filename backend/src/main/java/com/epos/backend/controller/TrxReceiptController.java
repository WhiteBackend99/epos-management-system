package com.epos.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.PrintReceiptRequest;
import com.epos.backend.model.dto.request.ReprintReceiptRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.TrxReceiptResponse;
import com.epos.backend.service.TrxReceiptService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class TrxReceiptController {

    private final TrxReceiptService trxReceiptService;

    @AuditLog(
        type = AuditType.RECEIPT,
        action = AuditAction.PRINT
    )
    @PostMapping(value = "/print")
    public ResponseEntity<ResponseData<TrxReceiptResponse>> print(@Valid @RequestBody PrintReceiptRequest request, HttpServletRequest servletRequest) {
        ResponseData<TrxReceiptResponse> response = ResponseData.<TrxReceiptResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Struk berhasil dicetak")
            .data(trxReceiptService.printReceipt(request, servletRequest))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.RECEIPT,
        action = AuditAction.REPRINT
    )
    @PostMapping(value = "/reprint")
    public ResponseEntity<ResponseData<TrxReceiptResponse>> reprint(@Valid @RequestBody ReprintReceiptRequest request, HttpServletRequest servletRequest) {
        ResponseData<TrxReceiptResponse> response = ResponseData.<TrxReceiptResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Struk berhasil dicetak ulang")
            .data(trxReceiptService.reprintReceipt(request, servletRequest))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.RECEIPT,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data-number/{receiptNo}")
    public ResponseEntity<ResponseData<TrxReceiptResponse>> getDataByReceiptNo(@PathVariable String receiptNo) {
        ResponseData<TrxReceiptResponse> response = ResponseData.<TrxReceiptResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Data struk berhasil ditemukan")
            .data(trxReceiptService.getByReceiptNo(receiptNo))
            .build();
        return ResponseEntity.ok(response);
    }

}
