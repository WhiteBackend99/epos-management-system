package com.epos.backend.service;

import java.util.List;

import com.epos.backend.model.dto.request.search.ReportSearchRequest;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.CashierReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.DailySalesSummaryResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.PaymentMethodSummaryResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.ProductSalesReportResponse;
import com.epos.backend.model.dto.response.SalesDashboardReportResponse.RefundSummaryResponse;

public interface ReportService {

    public SalesDashboardReportResponse getSalesDashboard(ReportSearchRequest request);
    public DailySalesSummaryResponse getDailySalesSummary(ReportSearchRequest request);
    public PaymentMethodSummaryResponse getPaymentMethodSummary(ReportSearchRequest request);
    public RefundSummaryResponse getRefundSummary(ReportSearchRequest request);
    public List<CashierReportResponse> getCashierReport(ReportSearchRequest request);
    public List<ProductSalesReportResponse> getProductSalesReport(ReportSearchRequest request);

}
