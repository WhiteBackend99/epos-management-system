package com.epos.backend.service.impl;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.epos.backend.model.entity.AuditTrail;
import com.epos.backend.repository.AuditTrailRepository;
import com.epos.backend.service.AuditTrailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditTrailServiceImpl implements AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    @Override
    @Async("audit-executor")
    public CompletableFuture<Void> saveAuditAsync(Long referenceId, String auditType, String action, String endpoint, String httpMethod, String ipAddress, String userAgent, Map<String, Object> request, Map<String, Object> response, Map<String, Object> errorPayload, Long status, String createdBy) {
        try {
            AuditTrail newData = AuditTrail.builder()
                .referencesId(referenceId)
                .auditType(auditType)
                .action(action)
                .endpoint(endpoint)
                .httpMethod(httpMethod)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .requestPayload(request)
                .responsePayload(response)
                .errorPayload(errorPayload)
                .status(status)
                .createdBy(createdBy)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

            auditTrailRepository.save(newData);
        } catch (Exception e) {
            log.error("Gagal simpan audit trail. Tipe={}, action={}, referencesId={}", auditType, action, referenceId);
        }
        return CompletableFuture.completedFuture(null);
    }
    

}
