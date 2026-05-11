package com.epos.backend.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AuditTrailService {

    public CompletableFuture<Void> saveAuditAsync(Long referenceId, String auditType, String action, String endpoint, String httpMethod, String ipAddress, String userAgent, Map<String, Object> request, Map<String, Object> response, Map<String, Object> errorPayload, Long status, String createdBy);

}
