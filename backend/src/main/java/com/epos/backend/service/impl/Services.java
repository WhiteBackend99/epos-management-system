package com.epos.backend.service.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.epos.backend.security.CurrentUserService;
import com.epos.backend.service.AutomationGeneratorServices;

import jakarta.servlet.http.HttpServletRequest;

@Service
public abstract class Services {

    @Autowired
    protected CurrentUserService currentUserService;

    @Autowired
    protected AutomationGeneratorServices automationGeneratorServices;

    /* -------------------------------- FUNCTION PROTECTED -------------------------------------- */
    protected String getCurrentUsername() {
        return currentUserService.getUsername();
    }

    protected String getClientIp(HttpServletRequest servletRequest) {
        String forwarded = servletRequest.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0];
        }
        return servletRequest.getRemoteAddr();
    }

    protected Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    protected Boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream().anyMatch(auth -> "ADMIN".equalsIgnoreCase(auth.getAuthority()) || "ROLE_ADMIN".equalsIgnoreCase(auth.getAuthority()));
    }
}
