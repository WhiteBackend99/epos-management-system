package com.epos.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.search.ReportSearchRequest;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.CashierReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.DailySalesSummaryResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.PaymentMethodSummaryResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.ProductSalesReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.RefundSummaryResponse;
import com.epos.backend.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @AuditLog(
        type = AuditType.REPORT,
        action = AuditAction.SALES_DASHBOARD
    )
    @GetMapping(value = "/sales-dashboard")
    public ResponseEntity<ResponseData<SalesDashboardReportResponse>> getDataSalesDashboard(@ModelAttribute ReportSearchRequest request) {
        ResponseData<SalesDashboardReportResponse> response = ResponseData.<SalesDashboardReportResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Dashboard laporan penjualan berhasil ditemukan")
            .data(reportService.getSalesDashboard(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.REPORT,
        action = AuditAction.DAILY_SALES
    )
    @GetMapping(value = "/daily-sales")
    public ResponseEntity<ResponseData<DailySalesSummaryResponse>> getDataDailySales(@ModelAttribute ReportSearchRequest request) {
        ResponseData<DailySalesSummaryResponse> response = ResponseData.<DailySalesSummaryResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Laporan penjualan harian berhasil ditemukan")
            .data(reportService.getDailySalesSummary(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.REPORT,
        action = AuditAction.PAYMENT_METHOD
    )
    @GetMapping(value = "/payment-method")
    public ResponseEntity<ResponseData<PaymentMethodSummaryResponse>> getDataPaymentMethod(@ModelAttribute ReportSearchRequest request) {
        ResponseData<PaymentMethodSummaryResponse> response = ResponseData.<PaymentMethodSummaryResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Laporan metode pembayaran berhasil ditemukan")
            .data(reportService.getPaymentMethodSummary(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.REPORT,
        action = AuditAction.REFUND_PAYMENT
    )
    @GetMapping(value = "/refund-summary")
    public ResponseEntity<ResponseData<RefundSummaryResponse>> getDataRefundSummary(@ModelAttribute ReportSearchRequest request) {
        ResponseData<RefundSummaryResponse> response = ResponseData.<RefundSummaryResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Laporan hasil pengembalian pembayaran berhasil ditemukan")
            .data(reportService.getRefundSummary(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.REPORT,
        action = AuditAction.CASHIER_REPORT
    )
    @GetMapping(value = "/cashier")
    public ResponseEntity<ResponseData<List<CashierReportResponse>>> getDataCashier(@ModelAttribute ReportSearchRequest request) {
        ResponseData<List<CashierReportResponse>> response = ResponseData.<List<CashierReportResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Laporan kumpulan data kasir berhasil diteumkan")
            .data(reportService.getCashierReport(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.REPORT,
        action = AuditAction.PRODUCT_SALES
    )
    @GetMapping(value = "/product-sales")
    public ResponseEntity<ResponseData<List<ProductSalesReportResponse>>> getDataProductSales(@ModelAttribute ReportSearchRequest request) {
        ResponseData<List<ProductSalesReportResponse>> response = ResponseData.<List<ProductSalesReportResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Laporan kumpulan data penjualan produk berhasil ditemukan")
            .data(reportService.getProductSalesReport(request))
            .build();
        return ResponseEntity.ok(response);
    }

}
