package com.epos.backend.service.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
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
}
