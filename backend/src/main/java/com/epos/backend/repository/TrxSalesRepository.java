package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epos.backend.model.entity.TrxSales;
import com.epos.backend.model.projection.CashierShiftSummaryProjection;

public interface TrxSalesRepository extends JpaRepository<TrxSales, Long> {

    public Optional<TrxSales> findBySalesNo(String salesNo);
    public Page<TrxSales> findBySalesNoContainingIgnoreCaseOrCustomerNameContainingIgnoreCase(String salesNo, String customerName, Pageable pageable);
    
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
                AND ts.status = 'SUCCESS'
            """, nativeQuery = true)
    public CashierShiftSummaryProjection getSalesSummaryByShiftId(Long shiftId);
}
