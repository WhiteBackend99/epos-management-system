package com.epos.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.epos.backend.model.dto.request.CreatePromoRequest;
import com.epos.backend.model.dto.request.PromoSimulationRequest;
import com.epos.backend.model.dto.request.UpdatePromoRequest;
import com.epos.backend.model.dto.response.PromoResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.result.AppliedPromoResult;
import com.epos.backend.service.PromoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoController {

    private final PromoService promoService;

    @AuditLog(
        type = AuditType.PROMO,
        action = AuditAction.CREATE
    )
    @PostMapping(value = "/create-data")
    public ResponseEntity<ResponseData<PromoResponse>> createData(@Valid @RequestBody CreatePromoRequest request) {
        ResponseData<PromoResponse> response = ResponseData.<PromoResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Promo berhasil dibuat")
            .data(promoService.createPromo(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PROMO,
        action = AuditAction.SIMULATE
    )
    @PostMapping(value = "/simulate")
    public ResponseEntity<ResponseData<AppliedPromoResult>> simulateData(@RequestBody PromoSimulationRequest request) {
        ResponseData<AppliedPromoResult> response = ResponseData.<AppliedPromoResult> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Simulasi promo berhasil")
            .data(promoService.simulate(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PROMO,
        action = AuditAction.UPDATE,
        referenceIdField = "id"
    )
    @PutMapping(value = "/update-data/{id}")
    public ResponseEntity<ResponseData<PromoResponse>> updateData(@PathVariable Long id, @Valid @RequestBody UpdatePromoRequest request) {
        ResponseData<PromoResponse> response = ResponseData.<PromoResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Promo berhasil diubah")
            .data(promoService.updatePromo(id, request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PROMO,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<PromoResponse>> getDataById(@PathVariable Long id) {
        ResponseData<PromoResponse> response = ResponseData.<PromoResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Promo berhasil ditemukan")
            .data(promoService.getDataById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.PROMO,
        action = AuditAction.VIEW
    )
    @GetMapping(value = "/get-all-data-active")
    public ResponseEntity<ResponseData<List<PromoResponse>>> getAllDataActive() {
        ResponseData<List<PromoResponse>> response = ResponseData.<List<PromoResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Data promo aktif berhasil ditemukan")
            .data(promoService.getAllActive())
            .build();
        return ResponseEntity.ok(response);
    }

}
