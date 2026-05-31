package com.epos.backend.model.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mst_system_parameter", schema = "public")
public class SystemParameter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "parameter_code", nullable = false, unique = true, length = 100)
    private String parameterCode;

    @Column(name = "parameter_name", nullable = false, length = 150)
    private String parameterName;

    @Column(name = "parameter_value", nullable = false, columnDefinition = "TEXT")
    private String parameterValue;

    @Column(name = "parameter_type", nullable = false, length = 50)
    private String parameterType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

}
