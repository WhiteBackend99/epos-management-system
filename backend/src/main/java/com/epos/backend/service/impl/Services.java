package com.epos.backend.service.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.epos.backend.security.CurrentUserService;

@Service
public abstract class Services {

    @Autowired
    protected CurrentUserService currentUserService;

    /* -------------------------------- FUNCTION PROTECTED -------------------------------------- */
    protected String getCurrentUsername() {
        return currentUserService.getUsername();
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
