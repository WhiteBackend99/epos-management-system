package com.epos.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.RedeemPointRequest;
import com.epos.backend.model.dto.response.RedeemPointResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.TrxLoyaltyPointService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class TrxLoyaltyPointController {

    private final TrxLoyaltyPointService trxLoyaltyPointService;

    @AuditLog(
        type = AuditType.LOYALITY_POINT,
        action = AuditAction.REDEEM
    )
    @PostMapping(value = "/redeem")
    public ResponseEntity<ResponseData<RedeemPointResponse>> redeemPoint(@Valid @RequestBody RedeemPointRequest request) {
        ResponseData<RedeemPointResponse> response = ResponseData.<RedeemPointResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Redeem point berhasil")
            .data(trxLoyaltyPointService.redeemPoint(request))
            .build();
        return ResponseEntity.ok(response);
    }

}
