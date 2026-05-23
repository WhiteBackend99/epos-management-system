package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.CashierShiftStatus;
import com.epos.backend.model.dto.request.CloseCashierShiftRequest;
import com.epos.backend.model.dto.request.OpenCashierShiftRequest;
import com.epos.backend.model.dto.request.search.TrxCashierShiftSearchRequest;
import com.epos.backend.model.dto.response.TrxCashierShiftResponse;
import com.epos.backend.model.entity.TrxCashierShift;
import com.epos.backend.model.projection.CashierShiftSummaryProjection;
import com.epos.backend.repository.TrxCashierShiftRepository;
import com.epos.backend.repository.TrxSalesRepository;
import com.epos.backend.repository.TrxSalesReturnRepository;
import com.epos.backend.service.TrxCashierShiftService;
import com.epos.backend.specification.TrxCashierShiftSpecification;
import com.epos.backend.util.GeneratorUtil;
import com.epos.backend.util.ParseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrxCashierShiftServiceImpl extends Services implements TrxCashierShiftService {
    
    private final TrxCashierShiftRepository trxCashierShiftRepository;
    private final TrxSalesRepository trxSalesRepository;
    private final TrxSalesReturnRepository trxSalesReturnRepository;

    private TrxCashierShiftResponse buildEntityToResponse(TrxCashierShift data) {
        return TrxCashierShiftResponse.builder()
            .id(data.getId())
            .shiftNo(data.getShiftNo())
            .cashierUsername(data.getCashierUsername())
            .openingCash(data.getOpeningCash())
            .expectedCash(data.getExpectedCash())
            .actualCash(data.getActualCash())
            .differenceAmount(data.getDifferenceAmount())
            .totalSalesAmount(data.getTotalSalesAmount())
            .totalRefundAmount(data.getTotalRefundAmount())
            .totalCashSales(data.getTotalCashSales())
            .totalQrisSales(data.getTotalQrisSales())
            .totalTransferSales(data.getTotalTransferSales())
            .totalDebitCardSales(data.getTotalDebitCardSales())
            .totalCashRefund(data.getTotalCashRefund())
            .totalQrisRefund(data.getTotalQrisRefund())
            .totalTransferRefund(data.getTotalTransferRefund())
            .totalDebitCardRefund(data.getTotalDebitCardRefund())
            .salesTransactionCount(data.getSalesTransactionCount())
            .returnTransactionCount(data.getReturnTransactionCount())
            .status(data.getStatus())
            .openedAt(data.getOpenedAt())
            .closedAt(data.getClosedAt())
            .createdBy(data.getCreatedBy())
            .createdAt(data.getCreatedAt())
            .updatedBy(data.getUpdatedBy())
            .updatedAt(data.getUpdatedAt())
            .build();
    }

    @Transactional
    @Override
    public TrxCashierShiftResponse openShift(OpenCashierShiftRequest request) {
        Boolean hasOpenShift = trxCashierShiftRepository.existsByCashierUsernameAndStatus(getCurrentUsername(), CashierShiftStatus.OPEN);
        if (hasOpenShift) {
            throw new IllegalArgumentException("Kasir masih memiliki shift yang sedang dijalankan");
        }

        TrxCashierShift newData = TrxCashierShift.builder()
            .shiftNo(GeneratorUtil.generateShiftNo())
            .cashierUsername(getCurrentUsername())
            .openingCash(ParseUtil.defaultAmount(request.getOpeningCash()))
            .expectedCash(ParseUtil.defaultAmount(request.getOpeningCash()))
            .actualCash(BigDecimal.ZERO)
            .differenceAmount(BigDecimal.ZERO)
            .totalSalesAmount(BigDecimal.ZERO)
            .totalRefundAmount(BigDecimal.ZERO)
            .totalCashSales(BigDecimal.ZERO)
            .totalQrisSales(BigDecimal.ZERO)
            .totalTransferSales(BigDecimal.ZERO)
            .totalDebitCardSales(BigDecimal.ZERO)
            .totalCashRefund(BigDecimal.ZERO)
            .totalQrisRefund(BigDecimal.ZERO)
            .totalTransferRefund(BigDecimal.ZERO)
            .totalDebitCardRefund(BigDecimal.ZERO)
            .salesTransactionCount(0l)
            .returnTransactionCount(0l)
            .status(CashierShiftStatus.OPEN)
            .openedAt(LocalDateTime.now())
            .openNotes(request.getOpenNotes())
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .build();

        return buildEntityToResponse(trxCashierShiftRepository.save(newData));
    }

    @Transactional
    @Override
    public TrxCashierShiftResponse closeShift(CloseCashierShiftRequest request) {
        TrxCashierShift data = trxCashierShiftRepository.findFirstByCashierUsernameAndStatusOrderByOpenedAtDesc(getCurrentUsername(), CashierShiftStatus.OPEN).orElseThrow(() -> new IllegalArgumentException("Tidak ada yang buka data shift kasir ini"));
        CashierShiftSummaryProjection salesSummary = trxSalesRepository.getSalesSummaryByShiftId(data.getId());
        CashierShiftSummaryProjection refundSummary = trxSalesReturnRepository.getReturnSummaryByShiftId(data.getId());
        BigDecimal totalCashSales = ParseUtil.defaultAmount(salesSummary.getTotalCashSales());
        BigDecimal totalCashRefund = ParseUtil.defaultAmount(refundSummary.getTotalCashRefund());
        BigDecimal expectedCash = data.getOpeningCash().add(totalCashSales).subtract(totalCashRefund);
        BigDecimal actualCash = ParseUtil.defaultAmount(request.getActualCash());
        BigDecimal differenceAmount = actualCash.subtract(expectedCash);

        data.setExpectedCash(expectedCash);
        data.setActualCash(actualCash);
        data.setDifferenceAmount(differenceAmount);
        data.setTotalSalesAmount(ParseUtil.defaultAmount(salesSummary.getTotalSalesAmount()));
        data.setTotalRefundAmount(ParseUtil.defaultAmount(refundSummary.getTotalRefundAmount()));
        data.setTotalCashSales(totalCashSales);
        data.setTotalQrisSales(ParseUtil.defaultAmount(salesSummary.getTotalQrisSales()));
        data.setTotalTransferSales(ParseUtil.defaultAmount(salesSummary.getTotalTransferSales()));
        data.setTotalDebitCardSales(ParseUtil.defaultAmount(salesSummary.getTotalDebitCardSales()));
        data.setTotalCashRefund(totalCashRefund);
        data.setTotalQrisRefund(ParseUtil.defaultAmount(refundSummary.getTotalQrisRefund()));
        data.setTotalTransferRefund(ParseUtil.defaultAmount(refundSummary.getTotalTransferRefund()));
        data.setTotalDebitCardRefund(ParseUtil.defaultAmount(refundSummary.getTotalDebitCardRefund()));
        data.setStatus(CashierShiftStatus.CLOSED);
        data.setClosedAt(LocalDateTime.now());
        data.setCloseNotes(request.getCloseNotes());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());

        return buildEntityToResponse(trxCashierShiftRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public TrxCashierShiftResponse getCurrentOpenShift() {
        TrxCashierShift data = trxCashierShiftRepository.findFirstByCashierUsernameAndStatusOrderByOpenedAtDesc(getCurrentUsername(), CashierShiftStatus.OPEN).orElseThrow(() -> new IllegalArgumentException("Tidak ada yang buka data shift kasir ini"));
        return buildEntityToResponse(data);
    }

    @Transactional(readOnly = true)
    @Override
    public TrxCashierShiftResponse getDataById(Long id) {
        TrxCashierShift data = trxCashierShiftRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Data shift kasir tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional(readOnly = true)
    @Override
    public TrxCashierShiftResponse getByShiftNo(String shiftNo) {
        TrxCashierShift data = trxCashierShiftRepository.findByShiftNo(shiftNo).orElseThrow(() -> new IllegalArgumentException("Data shift kasir tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TrxCashierShiftResponse> searchData(Pageable pageable, TrxCashierShiftSearchRequest request) {
        return trxCashierShiftRepository.findAll(TrxCashierShiftSpecification.filter(request), pageable).map(this::buildEntityToResponse);
    }

}
