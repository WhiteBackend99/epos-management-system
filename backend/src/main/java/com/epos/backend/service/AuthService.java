package com.epos.backend.service;

import com.epos.backend.model.dto.request.LoginRequest;
import com.epos.backend.model.dto.request.RegisterRequest;
import com.epos.backend.model.dto.response.AuthResponse;

public interface AuthService {

    public AuthResponse register(RegisterRequest request);
    public AuthResponse login(LoginRequest request);

}
