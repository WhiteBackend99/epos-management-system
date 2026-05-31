package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.SystemParameter;

import jakarta.persistence.LockModeType;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long>, JpaSpecificationExecutor<SystemParameter> {

    public Optional<SystemParameter> findByParameterCodeAndIsDeletedFalse(String parameterCode);
    public Boolean existsByParameterCodeAndIsDeletedFalse(String parameterCode);
    public Boolean existsByParameterCodeAndIdNotAndIsDeletedFalse(String parameterCode, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT sp
            FROM SystemParameter sp
            WHERE sp.id = :id
            AND sp.isDeleted = false
        """)
    public Optional<SystemParameter> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT sp
            FROM SystemParameter sp
            WHERE sp.parameterCode = :parameterCode
            AND sp.isDeleted = false
        """)
    public Optional<SystemParameter> findByCodeForUpdate(@Param("parameterCode") String parameterCode);

}
