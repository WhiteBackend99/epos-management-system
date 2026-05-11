package com.epos.backend.aspect;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.security.CurrentUserService;
import com.epos.backend.service.AuditTrailService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditTrailService auditTrailService;
    private final CurrentUserService currentUserService;
    private final ObjectMapper objectMapper;

    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private boolean isSensitveField(String fieldName) {
        String lower = fieldName.toLowerCase();

        return lower.contains("password") 
            || lower.contains("token") 
            || lower.contains("secret") 
            || lower.contains("authorization") 
            || lower.contains("pin") 
            || lower.contains("otp");
    }

    private boolean isIgnoredArgument(Object obj) {
        String className = obj.getClass().getName();

        return className.contains("HttpServletRequest")
            || className.contains("HttpServletResponse")
            || className.contains("MultipartFile")
            || className.contains("BindingResult")
            || className.contains("Principal");
    }

    private String getCurrentUsername() {
        try {
            String username = currentUserService.getUsername();
            return username != null && !username.isBlank() ? username : "SYSTEM";
        } catch (Exception e) {
            return "SYSTEM";
        }
    }

    private String getClientIp(HttpServletRequest servletRequest) {
        if (servletRequest == null) {
            return null;
        }

        String xForwardedFor = servletRequest.getHeader("X-Forwarded=For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = servletRequest.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        return servletRequest.getRemoteAddr();
    }

    private Long extractReferenceId(Object[] obj, String referenceIdField) {
        if (referenceIdField == null || referenceIdField.isBlank() || obj == null) {
            return null;
        }

        for (Object data : obj) {
            if (data == null || isIgnoredArgument(data)) {
                continue;
            }

            try {
                Field field = data.getClass().getDeclaredField(referenceIdField);
                field.setAccessible(true);

                Object value = field.get(data);
                return value != null ? ((Number) value).longValue() : null;
            } catch (Exception e) {
            }
        }
        return null;
    }

    private Object maskSensitiveData(Object args) {
        if (args instanceof Map<?,?> map) {
            Map<String, Object> masked = new LinkedHashMap<>();

            for (Map.Entry<?,?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                Object value = entry.getValue();

                if (isSensitveField(key)) {
                    masked.put(key, "**********");
                } else if (value instanceof Map<?,?>) {
                    masked.put(key, maskSensitiveData(value));
                } else {
                    masked.put(key, value);
                }
            }
            return masked;
        }
        return args;
    }

    private Map<String, Object> convertObjectToMap(Object obj) {
        try {
            if (obj == null) {
                return null;
            }

            Object target = null;
            if (obj instanceof ResponseEntity<?> responseEntity) {
                target = responseEntity.getBody();
            }

            Map<String, Object> converted = objectMapper.convertValue(target, new TypeReference<>() {});
            return (Map<String, Object>) maskSensitiveData(converted);
        } catch (Exception e) {
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("raw", String.valueOf(obj));
            fallback.put("convertError", e.getMessage());
            return fallback;
        }
    }

    private Map<String, Object> convertArgsToMap(Object[] args) {
        Map<String, Object> payload = new LinkedHashMap<>();

        if (args == null || args.length == 0) {
            return payload;
        }

        for (int i = 0; i < args.length; i++) {
            Object data = args[i];
            if (data == null || isIgnoredArgument(data)) {
                continue;
            }

            try {
                Object converted = objectMapper.convertValue(data, Object.class);
                payload.put("data"+i, maskSensitiveData(converted));
            } catch (Exception e) {
                payload.put("data"+i, String.valueOf(data));
            }
        }

        return payload;
    }

    @Around("@annotation(auditLog)")
    public Object audit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        log.info("Audit Log Trigered: type={}, action={}", auditLog.type(), auditLog.action());

        HttpServletRequest servletRequest = getCurrentRequest();
        String endpoint = servletRequest != null ? servletRequest.getRequestURI() : null;
        String httpMethod = servletRequest != null ? servletRequest.getMethod() : null;
        String ipAddress = getClientIp(servletRequest);
        String userAgent = servletRequest != null ? servletRequest.getHeader("User-Agent") : null;
        String createdBy = getCurrentUsername();
        
        Map<String, Object> request = auditLog.saveRequest() ? convertArgsToMap(joinPoint.getArgs()) : null;
        Long referenceId = extractReferenceId(joinPoint.getArgs(), auditLog.referenceIdField());

        try {
            Object proceed = joinPoint.proceed();
            Map<String, Object> response = auditLog.saveResponse() ? convertObjectToMap(proceed) : null;

            auditTrailService.saveAuditAsync(referenceId, auditLog.type().name(), auditLog.action().name(), endpoint, httpMethod, ipAddress, userAgent, request, response, null, Long.valueOf(HttpStatus.OK.value()), createdBy);
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getClass().getSimpleName());
            error.put("message", e.getMessage());
            auditTrailService.saveAuditAsync(referenceId, auditLog.type().name(), auditLog.action().name(), endpoint, httpMethod, ipAddress, userAgent, request, null, error, Long.valueOf(-100), createdBy);
            throw e;
        }
    }

}
