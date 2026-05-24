package com.epos.backend.model.dto.request;

import java.util.List;

import com.epos.backend.enums.RefundMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class TrxSalesReturnRequest {

    @NotNull(message = "Sales ID wajib diisi")
    private Long salesId;

    @NotBlank(message = "Alasan return wajib diisi")
    private String returnReason;

    private Boolean refund = false;
    private RefundMethod refundMethod;

    @Valid
    @NotEmpty(message = "Item return tidak boleh kosong")
    private List<TrxSalesReturnItemRequest> items;

    @Data
    @Builder
    public static class TrxSalesReturnItemRequest {

        @NotNull(message = "Detail Sales ID wajib diisi")
        private Long salesDetailId;

        @NotNull(message = "Kuantiti return wajib diisi")
        @Min(value = 1, message = "Kuantiti return minimal 1")
        private Long qtyReturn;
    }

}
