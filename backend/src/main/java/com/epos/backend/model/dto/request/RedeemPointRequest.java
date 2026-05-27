package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RedeemPointRequest {

    @NotNull(message = "Nomor penjualan wajib diisi")
    private String salesNo;

    @NotBlank(message = "Kode member wajib diisi")
    private String memberCode;

    @NotNull(message = "Jumlah point redeem wajib diisi")
    @Min(value = 1, message = "Jumlah point redeem minimal 1")
    private Long redeemPoint;
    
}
