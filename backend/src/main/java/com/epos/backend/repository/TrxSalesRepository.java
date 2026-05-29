package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.TrxSales;
import com.epos.backend.model.projection.CashierShiftSummaryProjection;

import jakarta.persistence.LockModeType;

public interface TrxSalesRepository extends JpaRepository<TrxSales, Long>, JpaSpecificationExecutor<TrxSales> {

    public Optional<TrxSales> findBySalesNo(String salesNo);
    
    @Query(value = """
            SELECT COALESCE(SUM(ts.GRAND_TOTAL), 0) AS totalSalesAmount
                ,COALESCE(SUM(CASE WHEN tsp.payment_method = 'CASH' THEN tsp.paid_amount ELSE 0 END), 0) AS totalCashSales
                ,COALESCE(SUM(CASE WHEN tsp.payment_method = 'QRIS' then tsp.paid_amount ELSE 0 END), 0) AS totalQrisSales
                ,COALESCE(SUM(CASE WHEN tsp.payment_method = 'TRANSFER' then tsp.paid_amount ELSE 0 END), 0) AS totalTransferSales
                ,COALESCE(SUM(CASE WHEN tsp.payment_method = 'DEBIT' then tsp.paid_amount ELSE 0 END), 0) AS totalDebitCardSales
                ,COUNT(DISTINCT ts.id) AS salesTransactionCount
            FROM trx_sales ts
                LEFT JOIN trx_sales_payment tsp ON tsp.sales_id = ts.id
            WHERE ts.cashier_shift_id = :shiftId
                AND ts.status = 'PAID'
        """, nativeQuery = true)
    public CashierShiftSummaryProjection getSalesSummaryByShiftId(@Param("shiftId") Long shiftId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT ts
            FROM TrxSales ts
            WHERE ts.salesNo = :salesNo
        """)
    public Optional<TrxSales> findBySalesNoForUpdate(@Param("salesNo") String salesNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT ts
            FROM TrxSales ts
            WHERE ts.id = :id
        """)
    public Optional<TrxSales> findByIdForUpdate(@Param("id") Long id);
}
