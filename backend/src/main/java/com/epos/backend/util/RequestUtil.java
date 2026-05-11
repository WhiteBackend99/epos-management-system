package com.epos.backend.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    private RequestUtil(){}

    public static String getIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
    
}
