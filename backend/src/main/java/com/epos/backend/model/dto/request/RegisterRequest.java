package com.epos.backend.model.dto.request;

import com.epos.backend.enums.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    private String fullName;

    @NotBlank(message = "Username tidak boleh kosong")
    private String username;

    @NotBlank(message = "Kata Sandi tidak boleh kosong")
    private String password;

    @NotNull(message = "Role tidak boleh kosong")
    private Role role;

}
