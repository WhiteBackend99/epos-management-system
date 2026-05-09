package com.epos.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.model.dto.request.LoginRequest;
import com.epos.backend.model.dto.request.RegisterRequest;
import com.epos.backend.model.dto.response.AuthResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseData<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseData.<AuthResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Pendaftaran Akun Berhasil")
                .data(authService.register(request))
                .build();
    }

    @PostMapping(value = "/login")
    public ResponseData<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseData.<AuthResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Login Berhasil")
                .data(authService.login(request))
                .build();
    } 
    
}
