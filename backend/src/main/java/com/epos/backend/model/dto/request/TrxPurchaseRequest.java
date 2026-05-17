package com.epos.backend.model.dto.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrxPurchaseRequest {

    @NotNull(message = "Supplier wajib diisi")
    private Long supplierId;

    @NotNull(message = "Tanggal pembelian wajib diisi")
    private Date purchaseDate;

    @DecimalMin(value = "0.0", message = "Diskon tidak boleh minus")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Pajak tidak boleh minus")
    private BigDecimal taxAmount;

    private String notes;
    
    @NotEmpty(message = "Item pembelian tidak boleh kosong")
    @Valid
    private List<TrxPurchaseItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrxPurchaseItemRequest {

        @NotNull(message = "Produk wajib diisi")
        private Long productId;

        @NotNull(message = "Kuantiti wajib diisi")
        @Min(value = 1, message = "Kuantiti minial 1")
        private Long qty;

        @NotNull(message = "Harga beli wajib diisi")
        @DecimalMin(value = "0.0", inclusive = false, message = "Harga belu harus lebih dari 0")
        private BigDecimal purchasePrice;
    }

}
