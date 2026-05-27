package com.epos.backend.model.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCustomerMemberRequest {

    @NotBlank(message = "Nama member wajib diisi")
    private String fullName;

    @NotBlank(message = "Nomor HP wajib diisi")
    private String phoneNumber;

    private String email;
    private String gender;
    private LocalDate birthDate;
    private String address;
    
}
