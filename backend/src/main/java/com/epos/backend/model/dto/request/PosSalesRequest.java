package com.epos.backend.model.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PosSalesRequest {

    private Long customerMemberId;
    private String customerName;
    private String promoCode;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;

    @Valid
    @NotEmpty(message = "Item penjualan tidak boleh kosong")
    private List<PosSalesItemRequest> items;

    @Data
    @Builder
    public static class PosSalesItemRequest {

        @NotNull(message = "Produk wajib diisi")
        private Long productId;

        @NotNull(message = "Kuantiti wajib diisi")
        @Min(value = 1, message = "Kuantiti minimal 1")
        private Long qty;
    }

}
