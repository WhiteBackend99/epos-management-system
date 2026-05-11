package com.epos.backend.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    /* -------------------------------- FUNCTION PRIVATE -------------------------------------- */
    private Boolean isWhiteListed(String path) {
        return path.startsWith("/api/auth")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/actuator")
            || path.startsWith("/error");
    }

    private void sectionWriteResponseAuth(HttpServletResponse response, String message) throws IOException {
        ResponseData<Object> body = ResponseData.builder()
                .success(false)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .data(null)
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isWhiteListed(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sectionWriteResponseAuth(response, "Token tidak ditemukan atau format token salah");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (!jwtService.isTokenValid(token)) {
            sectionWriteResponseAuth(response, "Token tidak valid atau sudah expired");
            return;
        }

        String username = jwtService.extractUsername(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
    
}
