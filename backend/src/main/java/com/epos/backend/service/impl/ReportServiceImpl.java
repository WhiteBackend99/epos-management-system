package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.PaymentMethod;
import com.epos.backend.model.dto.request.search.ReportSearchRequest;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.CashierReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.DailySalesSummaryResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.PaymentMethodSummaryResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.ProductSalesReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.RefundSummaryResponse;
import com.epos.backend.service.ReportService;
import com.epos.backend.util.ParseUtil;
import com.epos.backend.util.QueryUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl extends Services implements ReportService {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;  

    private ReportSearchRequest sectionRevampSearchRequest(ReportSearchRequest request) {
        ReportSearchRequest data = request == null ? new ReportSearchRequest() : request;

        if (data.getStartDate() == null) {
            data.setStartDate(LocalDate.now().atStartOfDay());
        }
        if (data.getEndDate() == null) {
            data.setEndDate(LocalDate.now().atTime(23, 59, 59));
        }
        
        if (!isAdmin()) {
            data.setCashierUsername(getCurrentUsername());
        }

        return data;
    }

    private MapSqlParameterSource params(ReportSearchRequest request) {
        String keyword = request.getKeyword();
        PaymentMethod paymentMethod = request.getPaymentMethod();
        return new MapSqlParameterSource()
            .addValue("startDate", request.getStartDate())
            .addValue("endDate", request.getEndDate())
            .addValue("cashierUsername", ParseUtil.blankToNull(request.getCashierUsername()))
            .addValue("shiftNo", ParseUtil.blankToNull(request.getShiftNo()))
            .addValue("paymentMethod", paymentMethod == null ? null : paymentMethod.name())
            .addValue("productId", request.getProductId())
            .addValue("keyword", keyword == null || keyword.isBlank() ? null  : "%" + keyword.toLowerCase() + "%");
    }

    private ProductSalesReportResponse mapToProductSalesReportResponse(ResultSet rs, int rowNum) throws SQLException {
        return ProductSalesReportResponse.builder()
            .productId(rs.getLong("product_id"))
            .productName(rs.getString("product_name"))
            .qtySold(rs.getLong("qty_sold"))
            .qtyReturned(rs.getLong("qty_returned"))
            .netQtySold(rs.getLong("net_qty_sold"))
            .grossSales(QueryUtil.bd(rs, "gross_sales"))
            .returnAmount(QueryUtil.bd(rs, "return_amount"))
            .netSales(QueryUtil.bd(rs, "net_sales"))
            .build();
    }

    @Transactional(readOnly = true)
    @Override
    public SalesDashboardReportResponse getSalesDashboard(ReportSearchRequest request) {
        request = sectionRevampSearchRequest(request);
        return SalesDashboardReportResponse.builder()
            .dailySummary(getDailySalesSummary(request))
            .paymentSummary(getPaymentMethodSummary(request))
            .refundSummary(getRefundSummary(request))
            .cashierReports(getCashierReport(request))
            .productSales(getProductSalesReport(request))
            .build();
    }

    @Transactional(readOnly = true)
    @Override
    public DailySalesSummaryResponse getDailySalesSummary(ReportSearchRequest request) {
        request = sectionRevampSearchRequest(request);
        String sql = """
                SELECT COALESCE(SUM(CASE WHEN ts.STATUS = 'PAID' THEN ts.grand_total ELSE 0 END), 0) AS gross_sales
                    ,COALESCE((
                        SELECT SUM(tsr.refund_amount)
                        FROM trx_sales_return tsr
                            LEFT JOIN trx_cashier_shift tcs ON tcs.id = tsr.cashier_shift_id
                        WHERE tsr.return_status = 'APPROVED'
                            AND tsr.refund_status = 'REFUNDED'
                            AND tsr.created_at BETWEEN :startDate and :endDate
                            AND (CAST(:cashierUsername AS VARCHAR) IS NULL OR tcs.cashier_username = CAST(:cashierUsername AS VARCHAR))
                            AND (CAST(:shiftNo AS VARCHAR) IS NULL OR tcs.shift_no = CAST(:shiftNo AS VARCHAR))
                            AND (
                                CAST(:keyword AS VARCHAR) IS NULL
                                OR LOWER(tsr.return_no) LIKE CAST(:keyword AS VARCHAR)
                                OR LOWER(tsr.sales_no) LIKE CAST(:keyword AS VARCHAR)
                                OR LOWER(COALESCE(tsr.customer_name, '')) LIKE CAST(:keyword AS VARCHAR)
                                OR LOWER(COALESCE(tcs.cashier_username, '')) LIKE CAST(:keyword AS VARCHAR)
                                OR LOWER(COALESCE(tcs.shift_no, '')) LIKE CAST(:keyword AS VARCHAR)
                            )
                    ), 0) AS total_refund
                    ,COUNT(DISTINCT CASE WHEN ts.status = 'PAID' THEN ts.id END) AS success_transaction_count
                    ,COUNT(DISTINCT CASE WHEN ts.status = 'CANCELLED' THEN ts.id END) AS cancelled_transaction_count
                    ,COALESCE((
                        SELECT COUNT(DISTINCT tsr2.id)
                        FROM trx_sales_return tsr2
                            LEFT JOIN trx_cashier_shift tcs2 ON tcs2.id = tsr2.cashier_shift_id
                        WHERE tsr2.return_status = 'APPROVED'
                            AND tsr2.created_at BETWEEN :startDate and :endDate
                            AND (CAST(:cashierUsername AS VARCHAR) IS NULL OR tcs2.cashier_username = CAST(:cashierUsername AS VARCHAR))
                            AND (CAST(:shiftNo AS VARCHAR) IS NULL OR tcs2.shift_no = CAST(:shiftNo AS VARCHAR))
                            AND (
                                CAST(:keyword AS VARCHAR) IS NULL
                                OR LOWER(tsr2.return_no) LIKE CAST(:keyword AS VARCHAR)
                                OR LOWER(tsr2.sales_no) LIKE CAST(:keyword AS VARCHAR)
                                OR LOWER(COALESCE(tsr2.customer_name, '')) LIKE CAST(:keyword AS VARCHAR)
                                OR LOWER(COALESCE(tcs2.cashier_username, '')) LIKE CAST(:keyword AS VARCHAR)
                                OR LOWER(COALESCE(tcs2.shift_no, '')) LIKE CAST(:keyword AS VARCHAR)
                            )
                    ), 0) AS return_transaction_count
                FROM trx_sales ts
                    LEFT JOIN trx_cashier_shift tcs ON tcs.id = ts.cashier_shift_id
                WHERE ts.created_at BETWEEN :startDate and :endDate
                    AND (CAST(:cashierUsername AS VARCHAR) IS NULL OR tcs.cashier_username = CAST(:cashierUsername AS VARCHAR))
                    AND (CAST(:shiftNo AS VARCHAR) IS NULL OR tcs.shift_no = CAST(:shiftNo AS VARCHAR))
                    AND (
                        CAST(:keyword AS VARCHAR) IS NULL
                        OR LOWER(ts.sales_no) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(ts.customer_name, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tcs.cashier_username, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tcs.shift_no, '')) LIKE CAST(:keyword AS VARCHAR)
                    )    
                """;
        return jdbcTemplate.queryForObject(sql, params(request), (rs, rowNum) -> {
            BigDecimal grossSales = QueryUtil.bd(rs, "gross_sales");
            BigDecimal totalRefund = QueryUtil.bd(rs, "total_refund");
            BigDecimal netSales = grossSales.subtract(totalRefund);
            Long successCount = rs.getLong("success_transaction_count");

            BigDecimal average = successCount == 0 ? BigDecimal.ZERO : grossSales.divide(BigDecimal.valueOf(successCount), 2, RoundingMode.HALF_UP);
            
            return DailySalesSummaryResponse.builder()
                .grossSales(grossSales)
                .totalRefund(totalRefund)
                .netSales(netSales)
                .successTransactionCount(successCount)
                .cancelledTransactionCount(rs.getLong("cancelled_transaction_count"))
                .returnTransactionCount(rs.getLong("return_transaction_count"))
                .averangeTransactoin(average)
                .build();
        });
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentMethodSummaryResponse getPaymentMethodSummary(ReportSearchRequest request) {
        request = sectionRevampSearchRequest(request);
        String sql = """
                SELECT COALESCE(SUM(CASE WHEN tsp.payment_method = 'CASH' THEN tsp.paid_amount ELSE 0 END), 0) AS cash
                    ,COALESCE(SUM(CASE WHEN tsp.payment_method = 'QRIS' THEN tsp.paid_amount ELSE 0 END), 0) AS qris
                    ,COALESCE(SUM(CASE WHEN tsp.payment_method = 'TRANSFER' THEN tsp.paid_amount ELSE 0 END), 0) AS transfer
                    ,COALESCE(SUM(CASE WHEN tsp.payment_method IN ('DEBIT', 'DEBIT_CARD') THEN tsp.paid_amount ELSE 0 END), 0) AS debit
                    ,COALESCE(SUM(tsp.paid_amount), 0) AS total
                FROM trx_sales_payment tsp
                    JOIN trx_sales ts ON ts.id = tsp.sales_id
                    LEFT JOIN trx_cashier_shift tcs on tcs.id = ts.cashier_shift_id
                WHERE ts.status = 'PAID'
                    AND ts.created_at BETWEEN :startDate AND :endDate
                    AND (CAST(:paymentMethod AS VARCHAR) IS NULL OR tsp.payment_method = CAST(:paymentMethod AS VARCHAR))
                    AND (CAST(:cashierUsername AS VARCHAR) IS NULL OR tcs.cashier_username = CAST(:cashierUsername AS VARCHAR))
                    AND (CAST(:shiftNo AS VARCHAR) IS NULL OR tcs.shift_no = CAST(:shiftNo AS VARCHAR))
                    AND (
                        CAST(:keyword AS VARCHAR) IS NULL
                        OR LOWER(ts.sales_no) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(ts.customer_name, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tcs.cashier_username, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tcs.shift_no, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(tsp.payment_method) LIKE CAST(:keyword AS VARCHAR)
                    )
                """;
        
        return jdbcTemplate.queryForObject(sql, params(request), (rs, rowNum) -> {
            return PaymentMethodSummaryResponse.builder()
                .cash(QueryUtil.bd(rs, "cash"))
                .qris(QueryUtil.bd(rs, "qris"))
                .transfer(QueryUtil.bd(rs, "transfer"))
                .debit(QueryUtil.bd(rs, "debit"))
                .total(QueryUtil.bd(rs, "total"))
                .build();
        });
    }

    @Transactional(readOnly = true)
    @Override
    public RefundSummaryResponse getRefundSummary(ReportSearchRequest request) {
        request = sectionRevampSearchRequest(request);
        String sql = """
                SELECT COALESCE(SUM(tsrp.refund_amount), 0) AS total_refund
                    ,COALESCE(SUM(CASE WHEN tsrp.refund_method = 'CASH' THEN tsrp.refund_amount ELSE 0 END), 0) AS cash_refund
                    ,COALESCE(SUM(CASE WHEN tsrp.refund_method = 'QRIS' THEN tsrp.refund_amount ELSE 0 END), 0) AS qris_refund
                    ,COALESCE(SUM(CASE WHEN tsrp.refund_method = 'TRANSFER' THEN tsrp.refund_amount ELSE 0 END), 0) AS transfer_refund
                    ,COALESCE(SUM(CASE WHEN tsrp.refund_method IN ('DEBIT', 'DEBIT_CARD') THEN tsrp.refund_amount ELSE 0 END), 0) AS debit_refund
                    ,COUNT(DISTINCT tsr.id) AS refund_transaction_count
                FROM trx_sales_return tsr
                    LEFT JOIN trx_sales_refund_payment tsrp ON tsrp.sales_return_id = tsr.id
                    LEFT JOIN trx_cashier_shift tcs ON tcs.id = tsr.cashier_shift_id
                WHERE tsr.return_status = 'APPROVED'
                    AND tsr.refund_status = 'REFUNDED'
                    AND tsr.created_at BETWEEN :startDate AND :endDate
                    AND (CAST(:paymentMethod AS VARCHAR) IS NULL OR tsrp.refund_method = CAST(:paymentMethod AS VARCHAR))
                    AND (CAST(:cashierUsername AS VARCHAR) IS NULL OR tcs.cashier_username = CAST(:cashierUsername AS VARCHAR))
                    AND (CAST(:shiftNo AS VARCHAR) IS NULL OR tcs.shift_no = CAST(:shiftNo AS VARCHAR))
                    AND (
                        CAST(:keyword AS VARCHAR) IS NULL
                        OR LOWER(tsr.return_no) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(tsr.sales_no) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tsr.customer_name, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tcs.cashier_username, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tcs.shift_no, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tsrp.refund_method, '')) LIKE CAST(:keyword AS VARCHAR)
                    )
                """;
        
        return jdbcTemplate.queryForObject(sql, params(request), (rs, rowNum) -> 
            RefundSummaryResponse.builder()
                .totalRefund(QueryUtil.bd(rs, "total_refund"))
                .cashRefund(QueryUtil.bd(rs, "cash_refund"))
                .qrisRefund(QueryUtil.bd(rs, "qris_refund"))
                .transferRefund(QueryUtil.bd(rs, "transfer_refund"))
                .debitRefund(QueryUtil.bd(rs, "debit_refund"))
                .refundTransactionCount(rs.getLong("refund_transaction_count"))
                .build()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<CashierReportResponse> getCashierReport(ReportSearchRequest request) {
        request = sectionRevampSearchRequest(request);
        String sql = """
                SELECT tcs.shift_no
                    ,tcs.cashier_username
                    ,tcs.opening_cash
                    ,tcs.expected_cash
                    ,tcs.actual_cash
                    ,tcs.difference_amount
                    ,tcs.total_sales_amount
                    ,tcs.total_refund_amount
                    ,tcs.sales_transaction_count
                    ,tcs.return_transaction_count
                    ,tcs.status
                FROM trx_cashier_shift tcs
                WHERE tcs.opened_at BETWEEN :startDate and :endDate
                    AND (CAST(:cashierUsername AS VARCHAR) IS NULL OR tcs.cashier_username = CAST(:cashierUsername AS VARCHAR))
                    AND (CAST(:shiftNo AS VARCHAR) IS NULL OR tcs.shift_no = CAST(:shiftNo AS VARCHAR))
                    AND (
                        CAST(:keyword AS VARCHAR) IS NULL
                        OR LOWER(tcs.shift_no) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(tcs.cashier_username) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tcs.open_notes, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(COALESCE(tcs.close_notes, '')) LIKE CAST(:keyword AS VARCHAR)
                        OR LOWER(tcs.status) LIKE CAST(:keyword AS VARCHAR)
                    )
                ORDER BY tcs.opened_at DESC
                """;
        
        return jdbcTemplate.query(sql, params(request), (rs, rowNum) -> 
            CashierReportResponse.builder()
                .shiftNo(rs.getString("shift_no"))
                .cashierUsername(rs.getString("cashier_username"))
                .openingCash(QueryUtil.bd(rs, "opening_cash"))
                .expectedCash(QueryUtil.bd(rs, "expected_cash"))
                .actualCash(QueryUtil.bd(rs, "actual_cash"))
                .differenceAmount(QueryUtil.bd(rs, "difference_amount"))
                .totalSalesAmount(QueryUtil.bd(rs, "total_sales_amount"))
                .totalRefundAmount(QueryUtil.bd(rs, "total_refund_amount"))
                .salesTransactionCount(rs.getLong("sales_transaction_count"))
                .returnTransactionCount(rs.getLong("return_transaction_count"))
                .status(rs.getString("status"))
                .build()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductSalesReportResponse> getProductSalesReport(ReportSearchRequest request) {
        request = sectionRevampSearchRequest(request);
        String sql = """
                WITH data_sales_product AS (
                    SELECT tsd.product_id
                        ,tsd.product_name_snapshot AS product_name
                        ,COALESCE(SUM(tsd.qty), 0) AS qty_sold
                        ,COALESCE(SUM(tsd.subtotal), 0) AS gross_sales
                    FROM trx_sales_detail tsd
                        JOIN trx_sales ts ON ts.id = tsd.sales_id
                        LEFT JOIN trx_cashier_shift tcs on tcs.id = ts.cashier_shift_id
                    WHERE ts.status = 'PAID'
                        AND ts.created_at BETWEEN :startDate AND :endDate
                        AND (CAST(:productId AS NUMERIC) IS NULL OR tsd.product_id = CAST(:productId AS NUMERIC))
                        AND (CAST(:cashierUsername AS VARCHAR) IS NULL OR tcs.cashier_username = CAST(:cashierUsername AS VARCHAR))
                        AND (CAST(:shiftNo AS VARCHAR) IS NULL OR tcs.shift_no = CAST(:shiftNo AS VARCHAR))
                        AND (
                            CAST(:keyword AS VARCHAR) IS NULL
                            OR LOWER(ts.sales_no) LIKE CAST(:keyword AS VARCHAR)
                            OR LOWER(COALESCE(ts.customer_name, '')) LIKE CAST(:keyword AS VARCHAR)
                            OR LOWER(tsd.product_name_snapshot) LIKE CAST(:keyword AS VARCHAR)
                            OR LOWER(COALESCE(tcs.cashier_username, '')) LIKE CAST(:keyword AS VARCHAR)
                            OR LOWER(COALESCE(tcs.shift_no, '')) LIKE CAST(:keyword AS VARCHAR)
                        )
                    GROUP BY tsd.product_id, tsd.product_name_snapshot
                ), data_return_product AS (
                    SELECT tsrd.product_id
                        ,COALESCE(SUM(tsrd.qty_return), 0) AS qty_returned
                        ,COALESCE(SUM(tsrd.subtotal_return), 0) AS return_amount
                    FROM trx_sales_return_detail tsrd
                        JOIN trx_sales_return tsr ON tsr.id = tsrd.sales_return_id
                        LEFT JOIN trx_cashier_shift tcs ON tcs.id = tsr.cashier_shift_id
                    WHERE tsr.return_status = 'APPROVED'
                        AND tsr.created_at BETWEEN :startDate AND :endDate
                        AND (CAST(:productId AS NUMERIC) IS NULL OR tsrd.product_id = CAST(:productId AS NUMERIC))
                        AND (CAST(:cashierUsername AS VARCHAR) IS NULL OR tcs.cashier_username = CAST(:cashierUsername AS VARCHAR))
                        AND (CAST(:shiftNo AS VARCHAR) IS NULL OR tcs.shift_no = CAST(:shiftNo AS VARCHAR))
                    GROUP BY tsrd.product_id
                )
                SELECT dsp.product_id
                    ,dsp.product_name
                    ,dsp.qty_sold
                    ,COALESCE(drp.qty_returned, 0) AS qty_returned
                    ,dsp.qty_sold - COALESCE(drp.qty_returned, 0) AS net_qty_sold
                    ,dsp.gross_sales
                    ,COALESCE(drp.return_amount, 0) AS return_amount
                    ,dsp.gross_sales - COALESCE(drp.return_amount, 0) AS net_sales
                FROM data_sales_product dsp
                    LEFT JOIN data_return_product drp ON drp.product_id = dsp.product_id
                ORDER BY net_qty_sold DESC, net_sales DESC
                """;

        return jdbcTemplate.query(sql, params(request), this::mapToProductSalesReportResponse);
    }

}
