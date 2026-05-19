package com.epos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epos.backend.model.entity.TrxSalesReturnDetail;

public interface TrxSalesReturnDetailRepository extends JpaRepository<TrxSalesReturnDetail, Long> {

    @Query("""
            SELECT COALESCE(SUM(d.qtyReturn), 0)
            FROM TrxSalesReturnDetail d
            WHERE d.salesDetail.id = :salesDetailId
                AND d.salesReturn.returnStatus <> 'CANCELLED'
            """)
    public Long getTotalReturnedQtyBySalesDetailId(Long salesDetailId);

}
