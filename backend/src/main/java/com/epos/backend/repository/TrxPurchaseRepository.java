package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.TrxPurchase;

public interface TrxPurchaseRepository extends JpaRepository<TrxPurchase, Long>, JpaSpecificationExecutor<TrxPurchase> {

    public Optional<TrxPurchase> findByIdAndIsDeletedFalse(Long id);

    @Query(value = "SELECT nextval('seq_purchase_no')", nativeQuery = true)
    public Long getNextPurchaseNoSequence();

    @Query(value = "SELECT nextval('seq_purchase_invoice_no')", nativeQuery = true)
    public Long getNextPurchaseInvoiceNoSequence();

    @Query("""
        SELECT p
        FROM TrxPurchase p
            LEFT JOIN FETCH p.details d 
            LEFT JOIN FETCH d.product
            LEFT JOIN FETCH p.supplier
        WHERE p.id = :id
            AND p.isDeleted = false
        """)
    public Optional<TrxPurchase> findDetailById(@Param("id") Long id);

}
