package com.epos.backend.model.dto.response;

import com.epos.backend.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private String tokenType;
    private String username;
    private Role role;

}
