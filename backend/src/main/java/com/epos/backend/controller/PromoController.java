package com.epos.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.epos.backend.model.dto.request.search.PromoSearchRequest;
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

    @AuditLog(type = AuditType.PROMO, action = AuditAction.CREATE)
    @PostMapping("/create-data")
    public ResponseEntity<ResponseData<PromoResponse>> createData(@Valid @RequestBody CreatePromoRequest request) {
        return ResponseEntity.ok(ResponseData.success("Promo berhasil dibuat", promoService.createPromo(request)));
    }

    @AuditLog(type = AuditType.PROMO, action = AuditAction.SIMULATE)
    @PostMapping("/simulate")
    public ResponseEntity<ResponseData<AppliedPromoResult>> simulateData(@RequestBody PromoSimulationRequest request) {
        return ResponseEntity.ok(ResponseData.success("Simulasi promo berhasil", promoService.simulate(request)));
    }

    @AuditLog(type = AuditType.PROMO, action = AuditAction.UPDATE, referenceIdField = "id")
    @PutMapping("/update-data/{id}")
    public ResponseEntity<ResponseData<PromoResponse>> updateData(@PathVariable Long id, @Valid @RequestBody UpdatePromoRequest request) {
        return ResponseEntity.ok(ResponseData.success("Promo berhasil diubah", promoService.updatePromo(id, request)));
    }

    @AuditLog(type = AuditType.PROMO, action = AuditAction.VIEW, referenceIdField = "id")
    @GetMapping("/get-data/{id}")
    public ResponseEntity<ResponseData<PromoResponse>> getDataById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Promo berhasil ditemukan", promoService.getDataById(id)));
    }

    @AuditLog(type = AuditType.PROMO, action = AuditAction.VIEW)
    @GetMapping("/get-all-data-active")
    public ResponseEntity<ResponseData<List<PromoResponse>>> getAllDataActive() {
        return ResponseEntity.ok(ResponseData.success("Data promo aktif berhasil ditemukan", promoService.getAllActive()));
    }

    @AuditLog(type = AuditType.PROMO, action = AuditAction.VIEW)
    @PostMapping("/search-data")
    public ResponseEntity<ResponseData<Page<PromoResponse>>> searchData(@RequestBody(required = false) PromoSearchRequest request) {
        return ResponseEntity.ok(ResponseData.success("Data promo berhasil ditemukan", promoService.searchData(request)));
    }

    @AuditLog(type = AuditType.PROMO, action = AuditAction.DELETE, referenceIdField = "id")
    @DeleteMapping("/delete-data/{id}")
    public ResponseEntity<ResponseData<PromoResponse>> deleteData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Promo berhasil dihapus", promoService.deletePromo(id)));
    }

    @AuditLog(type = AuditType.PROMO, action = AuditAction.ACTIVE, referenceIdField = "id")
    @PatchMapping("/activate-data/{id}")
    public ResponseEntity<ResponseData<PromoResponse>> activateData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Promo berhasil diaktifkan", promoService.activatePromo(id)));
    }

    @AuditLog(type = AuditType.PROMO, action = AuditAction.DEACTIVE, referenceIdField = "id")
    @PatchMapping("/deactivate-data/{id}")
    public ResponseEntity<ResponseData<PromoResponse>> deactivateData(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Promo berhasil dinonaktifkan", promoService.deactivatePromo(id)));
    }

}
