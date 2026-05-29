package com.epos.backend.model.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.epos.backend.enums.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PosSalesPaymentSettlementRequest {

    @Valid
    @NotEmpty(message = "Pembayaran tidak boleh kosong")
    private List<PosSalesPaymentRequest> payments;
    
    @Data
    @Builder
    public static class PosSalesPaymentRequest {

        @NotNull(message = "Tipe pembayaran wajib diisi")
        private PaymentMethod paymentMethod;

        @NotNull(message = "Nominal pembayaran wajib diisi")
        private BigDecimal paidAmount;
    }

}
