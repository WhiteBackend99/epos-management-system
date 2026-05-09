package com.epos.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {

    @NotBlank(message = "Username tidak boleh kosong")
    private String username;

    @NotBlank(message = "Kata Sandi tidak boleh kosong")
    private String password;

}
