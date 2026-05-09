package com.epos.backend.model.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
    
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
    
    @Column(name = "updated_by")
    private String updatedBy;

}
