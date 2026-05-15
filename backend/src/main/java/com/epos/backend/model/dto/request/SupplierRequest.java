package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {

    @NotBlank(message = "Nama Supplier wajib diisi")
    @Size(max = 255, message = "Nama Supplier maksimal 255 karakter")
    private String name;

    @Size(max = 50, message = "Nomor telepon maksimal 50 karakter")
    private String phone;

    @Email(message = "Format email tidak valid")
    @Size(max = 150, message = "Email maksimal 150 karakter")
    private String email;

    @Size(max = 150, message = "Nama PIC maksimal 150 karakter")
    private String picName;
    
    private String address;
    private String notes;
    private Boolean isActive;

}
