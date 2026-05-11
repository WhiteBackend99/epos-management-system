package com.epos.backend.model.entity;

import java.sql.Timestamp;
import java.util.Map;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_trail", schema = "public")
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "references_id")
    private Long referencesId;

    @Column(name = "audit_type", nullable = false, length = 100)
    private String auditType;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "http_method", length = 20)
    private String httpMethod;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Type(JsonType.class)
    @Column(name = "request_payload", columnDefinition = "jsonb")
    private Map<String, Object> requestPayload;

    @Type(JsonType.class)
    @Column(name = "response_payload", columnDefinition = "jsonb")
    private Map<String, Object> responsePayload;

    @Type(JsonType.class)
    @Column(name = "error_payload", columnDefinition = "jsonb")
    private Map<String, Object> errorPayload;

    @Column(name = "status")
    private Long status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private Timestamp createdAt;

}
