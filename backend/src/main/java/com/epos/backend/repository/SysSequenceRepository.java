package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.SysSequence;

import jakarta.persistence.LockModeType;

public interface SysSequenceRepository extends JpaRepository<SysSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT ss
            FROM SysSequence ss
            WHERE ss.sequenceCode = :sequenceCode
        """)
    public Optional<SysSequence> findBySequenceCodeForUpdate(@Param("sequenceCode") String sequenceCode);

}
