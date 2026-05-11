package com.epos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.AuditTrail;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

}
