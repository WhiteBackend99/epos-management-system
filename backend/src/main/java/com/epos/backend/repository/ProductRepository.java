package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.Product;

import jakarta.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    public boolean existsByNameIgnoreCaseAndIsDeletedFalse(String name);
    public boolean existsByNameIgnoreCaseAndIdNotAndIsDeletedFalse(String name, Long id);
    public boolean existsBySkuIgnoreCaseAndIsDeletedFalse(String sku);
    public boolean existsByBarcodeIgnoreCaseAndIsDeletedFalse(String barcode);
    public boolean existsByBarcodeIgnoreCaseAndIdNotAndIsDeletedFalse(String barcode, Long id);
    public Optional<Product> findByIdAndIsDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.id = :id
            AND p.isDeleted = false
        """)
    public Optional<Product> findActiveProductForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.sku = :sku
            AND p.isDeleted = false
        """)
    public Optional<Product> findBySkuForUpdate(@Param("sku") String sku);

}
