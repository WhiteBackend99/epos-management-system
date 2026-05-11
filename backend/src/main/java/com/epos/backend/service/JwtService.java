package com.epos.backend.service;

public interface JwtService {

    public String generateToken(String username);
    public String extractUsername(String token);
    public Boolean isTokenValid(String token);

}
