package com.epos.backend.service.impl;

import java.sql.Timestamp;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.epos.backend.model.dto.request.LoginRequest;
import com.epos.backend.model.dto.request.RegisterRequest;
import com.epos.backend.model.dto.response.AuthResponse;
import com.epos.backend.model.entity.User;
import com.epos.backend.repository.UserRepository;
import com.epos.backend.service.AuthService;
import com.epos.backend.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }

        var newUser = User.builder()
            .fullName(request.getFullName())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .isActive(true)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .createdBy("USER")
            .updatedAt(null)
            .updatedBy(null)
            .build();

        userRepository.save(newUser);
        String token = jwtService.generateToken(newUser.getUsername());

        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .username(newUser.getUsername())
            .role(newUser.getRole())
            .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        
        String token = jwtService.generateToken(user.getUsername());

        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .username(user.getUsername())
            .role(user.getRole())
            .build();
    }

}
