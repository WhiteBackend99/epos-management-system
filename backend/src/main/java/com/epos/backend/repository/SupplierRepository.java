package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.Supplier;

import jakarta.persistence.LockModeType;

public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {

    public boolean existsByNameIgnoreCaseAndIsDeletedFalse(String name);
    public boolean existsByPhoneAndIsDeletedFalse(String phone);
    public boolean existsByEmailIgnoreCaseAndIsDeletedFalse(String email);
    public boolean existsByNameIgnoreCaseAndIdNotAndIsDeletedFalse(String name, Long id);
    public boolean existsByPhoneAndIdNotAndIsDeletedFalse(String phone, Long id);
    public boolean existsByEmailIgnoreCaseAndIdNotAndIsDeletedFalse(String email, Long id);
    public Optional<Supplier> findByIdAndIsDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Supplier s
            WHERE s.id = :id
            AND s.isDeleted = false
        """)
    public Optional<Supplier> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Supplier s
            WHERE s.supplierCode = :supplierCode
            AND s.isDeleted = false
        """)
    public Optional<Supplier> findBySupplierCodeForUpdate(@Param("supplierCode") String supplierCode);

}
