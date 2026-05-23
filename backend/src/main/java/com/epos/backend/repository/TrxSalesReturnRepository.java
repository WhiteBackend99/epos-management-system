package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.epos.backend.model.entity.TrxSalesReturn;
import com.epos.backend.model.projection.CashierShiftSummaryProjection;

public interface TrxSalesReturnRepository extends JpaRepository<TrxSalesReturn, Long>, JpaSpecificationExecutor<TrxSalesReturn> {

    public Optional<TrxSalesReturn> findByReturnNo(String returnNo);
 
    @Query(value = """
            SELECT COALESCE(SUM(tsr.refund_amount), 0) AS totalRefundAmount
                ,COALESCE(SUM(CASE WHEN tsrp.refund_method = 'CASH' THEN tsrp.refund_amount ELSE 0 END), 0) AS totalCashRefund
                ,COALESCE(SUM(CASE WHEN tsrp.refund_method = 'QRIS' then tsrp.refund_amount ELSE 0 END), 0) AS totalQrisRefund
                ,COALESCE(SUM(CASE WHEN tsrp.refund_method = 'TRANSFER' then tsrp.refund_amount ELSE 0 END), 0) AS totalTransferRefund
                ,COALESCE(SUM(CASE WHEN tsrp.refund_method = 'DEBIT' then tsrp.refund_amount ELSE 0 END), 0) AS totalDebitCardRefund
                ,COUNT(DISTINCT tsr.id) AS returnTransactionCount
            FROM trx_sales_return tsr
                LEFT JOIN trx_sales_refund_payment tsrp ON tsrp.sales_return_id = tsr.id
            WHERE tsr.cashier_shift_id = :shiftId
                AND tsr.return_status = 'APPROVED'
                AND tsr.return_status = 'REFUNDED'
            """, nativeQuery = true)
    public CashierShiftSummaryProjection getReturnSummaryByShiftId(Long shiftId);
}
