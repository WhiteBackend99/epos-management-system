package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.epos.backend.model.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {

    public boolean existsByNameIgnoreCaseAndIsDeletedFalse(String name);
    public boolean existsByPhoneAndIsDeletedFalse(String phone);
    public boolean existsByEmailIgnoreCaseAndIsDeletedFalse(String email);
    public boolean existsByNameIgnoreCaseAndIdNotAndIsDeletedFalse(String name, Long id);
    public boolean existsByPhoneAndIdNotAndIsDeletedFalse(String phone, Long id);
    public boolean existsByEmailIgnoreCaseAndIdNotAndIsDeletedFalse(String email, Long id);
    public Optional<Supplier> findByIdAndIsDeletedFalse(Long id);
    
    @Query(value = "select nextval('seq_supplier_code')", nativeQuery = true)
    public Long getNextSupplierCodeSequence();

}
