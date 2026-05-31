package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.CashierShiftStatus;
import com.epos.backend.enums.PaymentStatus;
import com.epos.backend.enums.SalesStatus;
import com.epos.backend.enums.StockMovementType;
import com.epos.backend.model.dto.context.PromoCalculationContext;
import com.epos.backend.model.dto.context.PromoCalculationContext.PromoCalculationItem;
import com.epos.backend.model.dto.request.PosSalesCancelRequest;
import com.epos.backend.model.dto.request.PosSalesPaymentSettlementRequest;
import com.epos.backend.model.dto.request.PosSalesPaymentSettlementRequest.PosSalesPaymentRequest;
import com.epos.backend.model.dto.request.PosSalesRequest;
import com.epos.backend.model.dto.request.PosSalesRequest.PosSalesItemRequest;
import com.epos.backend.model.dto.request.search.PosSalesSearchRequest;
import com.epos.backend.model.dto.response.PosSalesResponse;
import com.epos.backend.model.dto.response.PosSalesResponse.PosSalesDetailResponse;
import com.epos.backend.model.dto.response.PosSalesResponse.PosSalesPaymentResponse;
import com.epos.backend.model.dto.result.AppliedPromoResult;
import com.epos.backend.model.entity.CustomerMember;
import com.epos.backend.model.entity.Product;
import com.epos.backend.model.entity.Promo;
import com.epos.backend.model.entity.TrxCashierShift;
import com.epos.backend.model.entity.TrxSales;
import com.epos.backend.model.entity.TrxSalesDetail;
import com.epos.backend.model.entity.TrxSalesPayment;
import com.epos.backend.model.entity.TrxSalesPromo;
import com.epos.backend.model.entity.TrxStockMovement;
import com.epos.backend.repository.CustomerMemberRepository;
import com.epos.backend.repository.ProductRepository;
import com.epos.backend.repository.PromoRepository;
import com.epos.backend.repository.TrxCashierShiftRepository;
import com.epos.backend.repository.TrxSalesPromoRepository;
import com.epos.backend.repository.TrxSalesRepository;
import com.epos.backend.repository.TrxStockMovementRepository;
import com.epos.backend.service.PosSalesService;
import com.epos.backend.service.PromoEngineService;
import com.epos.backend.service.TrxLoyaltyPointService;
import com.epos.backend.specification.TrxSalesSpecification;
import com.epos.backend.util.ParseUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PosSalesServiceImpl extends Services implements PosSalesService {
    
    private final TrxSalesRepository trxSalesRepository;
    private final TrxStockMovementRepository trxStockMovementRepository;
    private final TrxCashierShiftRepository trxCashierShiftRepository;
    private final TrxSalesPromoRepository trxSalesPromoRepository;
    private final ProductRepository productRepository;
    private final PromoRepository promoRepository;
    private final CustomerMemberRepository customerMemberRepository;
    private final PromoEngineService promoEngineService;
    private final TrxLoyaltyPointService trxLoyaltyPointService;

    private PosSalesResponse buildEntityToResponse(TrxSales data) {
        List<PosSalesDetailResponse> details = data.getDetails() == null? List.of() : data.getDetails().stream().map(this::buildEntityToResponseDetail).toList();
        List<PosSalesPaymentResponse> payments = data.getPayments() == null ? List.of() : data.getPayments().stream().map(this::buildEntityToResponsePayment).toList();

        return PosSalesResponse.builder()
                .id(data.getId())
                .salesNo(data.getSalesNo())
                .customerName(data.getCustomerName())
                .customerMemberId(data.getCustomerMember() == null ? null : data.getCustomerMember().getId())
                .memberCodeSnapshot(data.getMemberCodeSnapshot())
                .memberNameSnapshot(data.getMemberNameSnapshot())
                .subtotal(data.getSubtotal())
                .manualDiscountAmount(data.getManualDiscountAmount())
                .promoDiscountAmount(data.getPromoDiscountAmount())
                .discountAmount(data.getDiscountAmount())
                .taxAmount(data.getTaxAmount())
                .grandTotalBeforePoint(data.getGrandTotalBeforePoint())
                .pointDiscountAmount(data.getPointDiscountAmount())
                .grandTotal(data.getGrandTotal())
                .paidAmount(data.getPaidAmount())
                .changeAmount(data.getChangeAmount())
                .pointEarned(data.getPointEarned())
                .pointRedeemed(data.getPointRedeemed())
                .loyaltyProcessedFlag(data.getLoyaltyProcessedFlag())
                .status(data.getStatus())
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())
                .updatedBy(data.getUpdatedBy())
                .updatedAt(data.getUpdatedAt())
                .cancelledBy(data.getCancelledBy())
                .cancelledAt(data.getCancelledAt())
                .cancelReason(data.getCancelReason())
                .customerMemberId(data.getCustomerMember() == null ? null : data.getCustomerMember().getId())
                .memberCodeSnapshot(data.getMemberCodeSnapshot())
                .memberNameSnapshot(data.getMemberNameSnapshot())
                .manualDiscountAmount(data.getManualDiscountAmount())
                .promoDiscountAmount(data.getPromoDiscountAmount())
                .grandTotalBeforePoint(data.getGrandTotalBeforePoint())
                .pointDiscountAmount(data.getPointDiscountAmount())
                .pointEarned(data.getPointEarned())
                .pointRedeemed(data.getPointRedeemed())
                .loyaltyProcessedFlag(data.getLoyaltyProcessedFlag())
                .paidAt(data.getPaidAt())
                .details(details)
                .payments(payments)
            .build();
    }

    private PosSalesDetailResponse buildEntityToResponseDetail(TrxSalesDetail data) {
        return PosSalesDetailResponse.builder()
                .productId(data.getProduct().getId())
                .productName(data.getProductNameSnapshot())
                .price(data.getPriceSnapshot())
                .qty(data.getQty())
                .subtotal(data.getSubtotal())
            .build();
    }

    private PosSalesPaymentResponse buildEntityToResponsePayment(TrxSalesPayment data) {
        return PosSalesPaymentResponse.builder()
                .paymentMethod(data.getPaymentMethod())
                .paymentStatus(data.getPaymentStatus())
                .paidAmount(data.getPaidAmount())
            .build();
    }

    private void validationDraftRequest(PosSalesRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) throw new IllegalArgumentException("Item penjualan tidak boleh kosong");

        Set<Long> productIds = new HashSet<>();
        for (PosSalesItemRequest requestItem : request.getItems()) {
            if (!productIds.add(requestItem.getProductId())) throw new IllegalArgumentException("Produk duplikat dalam transaksi: " + requestItem.getProductId());
        }
    }

    private void validationPaymentRequest(PosSalesPaymentSettlementRequest request) {
        if (request.getPayments() == null || request.getPayments().isEmpty()) throw new IllegalArgumentException("Pembayaran tidak boleh kosong");

        for (PosSalesPaymentRequest payment : request.getPayments()) {
            if (payment.getPaidAmount() == null || payment.getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Nominal pembayaran harus lebih dari 0");
        }
    }

    private void validationProductCanBeSold(Product data, Long qty) {
        if (data.getSellingPrice() == null || data.getSellingPrice().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Harga jual produk belum valid: " + data.getName());

        if (data.getStock() == null || data.getStock() < qty) throw new IllegalArgumentException("Stok produk " + data.getName() + " tidak mencukupi");
    }

    private void validationGrandTotal(BigDecimal grandTotal) {
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Grand total tidak boleh kurang dari 0");
    }

    private void updatePromoUsage(TrxSales sales) {
        List<TrxSalesPromo> salesPromos = trxSalesPromoRepository.findBySalesNo(sales.getSalesNo());

        for (TrxSalesPromo salesPromo : salesPromos) {
            Promo dataPromo = promoRepository.findByIdForUpdate(salesPromo.getPromoId())
                .orElseThrow(() -> new IllegalArgumentException("Promo tidak ditemukan"));

            dataPromo.setCurrentUsage(dataPromo.getCurrentUsage() + 1);
            dataPromo.setUpdatedBy(getCurrentUsername());
            dataPromo.setUpdatedAt(now());

            promoRepository.save(dataPromo);
        }
    }

    private void reverseStock(TrxSales data, PosSalesCancelRequest request) {
        for (TrxSalesDetail dataDetail : data.getDetails()) {
            Product dataProduct = productRepository.findActiveProductForUpdate(dataDetail.getProduct().getId()).orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan atau sudah tidak aktif"));

            Long qtyBefore = dataProduct.getStock();
            Long qtyChange = dataDetail.getQty();
            Long qtyAfter = qtyBefore + qtyChange;

            dataProduct.setStock(qtyAfter);
            dataProduct.setUpdatedBy(getCurrentUsername());
            dataProduct.setUpdatedAt(now());
            productRepository.save(dataProduct);

            TrxStockMovement newDataStockMovement = TrxStockMovement.builder()
                    .product(dataProduct)
                    .movementType(StockMovementType.SALES_CANCEL_REVERSAL)
                    .qtyBefore(qtyBefore)
                    .qtyChange(qtyChange)
                    .qtyAfter(qtyAfter)
                    .referenceNo(data.getSalesNo())
                    .notes("Cancel POS sales transaction. Reason: " + request.getCancelReason())
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                    .externalReferenceNo(null)
                .build();

            trxStockMovementRepository.save(newDataStockMovement);
        }
    }

    @Transactional
    @Override
    public PosSalesResponse createDraftSales(PosSalesRequest request) {
        TrxCashierShift dataCashierShift = trxCashierShiftRepository.findFirstByCashierUsernameAndStatusOrderByOpenedAtDesc(getCurrentUsername(), CashierShiftStatus.OPEN).orElseThrow(() -> new IllegalArgumentException("Kasir belum membuka shift"));

        validationDraftRequest(request);

        String salesNo = automationGeneratorServices.generateSalesNo();
        List<TrxSalesDetail> salesDetails = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        TrxSales newData = TrxSales.builder()
                .salesNo(salesNo)
                .customerName(request.getCustomerName())
                .subtotal(BigDecimal.ZERO)
                .manualDiscountAmount(ParseUtil.defaultAmount(request.getDiscountAmount()))
                .promoDiscountAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(ParseUtil.defaultAmount(request.getTaxAmount()))
                .grandTotalBeforePoint(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .changeAmount(BigDecimal.ZERO)
                .pointEarned(0L)
                .pointRedeemed(0L)
                .pointDiscountAmount(BigDecimal.ZERO)
                .loyaltyProcessedFlag(false)
                .status(SalesStatus.DRAFT)
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .cashierShift(dataCashierShift)
                .details(new ArrayList<>())
                .payments(new ArrayList<>())
            .build();

        if (request.getCustomerMemberId() != null) {
            CustomerMember dataMember = customerMemberRepository.findById(request.getCustomerMemberId()).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));
            newData.setCustomerMember(dataMember);
            newData.setMemberCodeSnapshot(dataMember.getMemberCode());
            newData.setMemberNameSnapshot(dataMember.getFullName());
        }

        for (PosSalesItemRequest requestItem : request.getItems()) {
            Product dataProduct = productRepository.findById(requestItem.getProductId()).orElseThrow(() -> new EntityNotFoundException("Produk tidak ditemukan: " + requestItem.getProductId()));

            validationProductCanBeSold(dataProduct, requestItem.getQty());

            BigDecimal price = dataProduct.getSellingPrice();
            BigDecimal itemSubtotal = price.multiply(BigDecimal.valueOf(requestItem.getQty()));

            TrxSalesDetail newDataDetail = TrxSalesDetail.builder()
                    .sales(newData)
                    .product(dataProduct)
                    .productNameSnapshot(dataProduct.getName())
                    .priceSnapshot(price)
                    .qty(requestItem.getQty())
                    .subtotal(itemSubtotal)
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build();

            salesDetails.add(newDataDetail);
            subtotal = subtotal.add(itemSubtotal);
        }

        PromoCalculationContext promoContext = PromoCalculationContext.builder()
                .subtotal(subtotal)
                .paymentMethod(null)
                .promoCode(request.getPromoCode())
                .customerName(request.getCustomerName())
                .items(salesDetails.stream()
                    .map(detail -> PromoCalculationItem.builder()
                            .productId(detail.getProduct().getId())
                            .categoryId(detail.getProduct().getCategory().getId())
                            .qty(detail.getQty())
                            .price(detail.getPriceSnapshot())
                            .subtotal(detail.getSubtotal())
                        .build())
                    .toList())
            .build();

        AppliedPromoResult promoResult;
        if (request.getPromoCode() != null && !request.getPromoCode().isBlank()) {
            promoResult = promoEngineService.calculateManualCodePromo(promoContext, request.getPromoCode());
        } else {
            promoResult = promoEngineService.calculateBestPromo(promoContext);
        }

        BigDecimal promoDiscount = ParseUtil.defaultAmount(promoResult.getDiscountAmount());
        BigDecimal manualDiscount = ParseUtil.defaultAmount(request.getDiscountAmount());
        BigDecimal totalDiscount = manualDiscount.add(promoDiscount);
        BigDecimal tax = ParseUtil.defaultAmount(request.getTaxAmount());
        BigDecimal grandTotalBeforePoint = subtotal.subtract(totalDiscount).add(tax);

        validationGrandTotal(grandTotalBeforePoint);

        newData.setSubtotal(subtotal);
        newData.setManualDiscountAmount(manualDiscount);
        newData.setPromoDiscountAmount(promoDiscount);
        newData.setDiscountAmount(totalDiscount);
        newData.setTaxAmount(tax);
        newData.setGrandTotalBeforePoint(grandTotalBeforePoint);
        newData.setGrandTotal(grandTotalBeforePoint);
        newData.setDetails(salesDetails);

        TrxSales dataSales = trxSalesRepository.save(newData);

        if (Boolean.TRUE.equals(promoResult.getApplied())) {
            trxSalesPromoRepository.save(
                TrxSalesPromo.builder()
                        .salesId(dataSales.getId())
                        .salesNo(dataSales.getSalesNo())
                        .promoId(promoResult.getPromoId())
                        .promoNo(promoResult.getPromoNo())
                        .promoName(promoResult.getPromoName())
                        .promoCode(promoResult.getPromoCode())
                        .discountAmount(promoResult.getDiscountAmount())
                        .promoSnapshot(promoResult.getPromoSnapshot())
                        .createdBy(getCurrentUsername())
                        .createdAt(now())
                    .build()
            );
        }

        return buildEntityToResponse(dataSales);
    }

    @Transactional
    @Override
    public PosSalesResponse settlePayment(String salesNo, PosSalesPaymentSettlementRequest request) {
        if (request.getPayments() == null || request.getPayments().isEmpty()) throw new IllegalArgumentException("Pembayaran tidak boleh kosong");

        TrxSales dataSales = trxSalesRepository.findBySalesNoForUpdate(salesNo).orElseThrow(() -> new IllegalArgumentException("Transaksi sales tidak ditemukan"));

        if (!SalesStatus.DRAFT.equals(dataSales.getStatus())) throw new IllegalArgumentException("Hanya transaksi DRAFT yang dapat dibayar");

        BigDecimal paidAmount = request.getPayments().stream().map(PosSalesPaymentRequest::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (paidAmount.compareTo(dataSales.getGrandTotal()) < 0) throw new IllegalArgumentException("Nominal pembayaran kurang dari grand total");

        for (TrxSalesDetail detail : dataSales.getDetails()) {
            Product product = productRepository.findActiveProductForUpdate(detail.getProduct().getId()).orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan atau sudah tidak aktif"));

            validationProductCanBeSold(product, detail.getQty());

            Long qtyBefore = product.getStock();
            Long qtyChange = detail.getQty() * -1;
            Long qtyAfter = qtyBefore + qtyChange;

            product.setStock(qtyAfter);
            product.setUpdatedBy(getCurrentUsername());
            product.setUpdatedAt(now());
            productRepository.save(product);

            trxStockMovementRepository.save(
                TrxStockMovement.builder()
                        .product(product)
                        .movementType(StockMovementType.SALES_OUT)
                        .qtyBefore(qtyBefore)
                        .qtyChange(qtyChange)
                        .qtyAfter(qtyAfter)
                        .referenceNo(dataSales.getSalesNo())
                        .notes("POS sales payment settlement " + dataSales.getSalesNo())
                        .createdBy(getCurrentUsername())
                        .createdAt(now())
                        .externalReferenceNo(null)
                    .build()
            );
        }

        dataSales.getPayments().clear();

        List<TrxSalesPayment> payments = request.getPayments().stream()
            .map(p -> TrxSalesPayment.builder()
                    .sales(dataSales)
                    .paymentMethod(p.getPaymentMethod())
                    .paymentStatus(PaymentStatus.PAID)
                    .paidAmount(p.getPaidAmount())
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build())
            .toList();

        dataSales.getPayments().addAll(payments);
        dataSales.setPaidAmount(paidAmount);
        dataSales.setChangeAmount(paidAmount.subtract(dataSales.getGrandTotal()));
        dataSales.setPaidAt(now());
        dataSales.setStatus(SalesStatus.PAID);
        dataSales.setUpdatedBy(getCurrentUsername());
        dataSales.setUpdatedAt(now());

        TrxSales saved = trxSalesRepository.save(dataSales);

        trxLoyaltyPointService.processEarnPoint(saved);
        updatePromoUsage(saved);

        return buildEntityToResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public PosSalesResponse getSalesById(Long id) {
        TrxSales data = trxSalesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transaksi penjualan tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional(readOnly = true)
    @Override
    public PosSalesResponse getSalesByNo(String salesNo) {
        TrxSales data = trxSalesRepository.findBySalesNo(salesNo).orElseThrow(() -> new EntityNotFoundException("Transaksi penjualan tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional
    @Override
    public PosSalesResponse cancelSales(Long id, PosSalesCancelRequest request) {
        TrxSales data = trxSalesRepository.findByIdForUpdate(id).orElseThrow(() -> new EntityNotFoundException("Transaksi penjualan tidak ditemukan"));

        if (SalesStatus.CANCELLED.equals(data.getStatus())) throw new IllegalArgumentException("Transaksi sudah dibatalkan");

        if (SalesStatus.PAID.equals(data.getStatus())) {
            for (TrxSalesDetail detail : data.getDetails()) {
                Product product = productRepository.findActiveProductForUpdate(detail.getProduct().getId()).orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan atau sudah tidak aktif"));

                Long qtyBefore = product.getStock();
                Long qtyChange = detail.getQty();
                Long qtyAfter = qtyBefore + qtyChange;

                product.setStock(qtyAfter);
                product.setUpdatedBy(getCurrentUsername());
                product.setUpdatedAt(now());
                productRepository.save(product);

                trxStockMovementRepository.save(
                    TrxStockMovement.builder()
                            .product(product)
                            .movementType(StockMovementType.SALES_CANCEL_REVERSAL)
                            .qtyBefore(qtyBefore)
                            .qtyChange(qtyChange)
                            .qtyAfter(qtyAfter)
                            .referenceNo(data.getSalesNo())
                            .notes("Cancel POS sales transaction. Reason: " + request.getCancelReason())
                            .createdBy(getCurrentUsername())
                            .createdAt(now())
                            .externalReferenceNo(null)
                        .build()
                );
            }
        }

        trxLoyaltyPointService.reverseEarnPoint(data.getSalesNo());
        trxLoyaltyPointService.reverseRedeemPoint(data.getSalesNo());

        data.setStatus(SalesStatus.CANCELLED);
        data.setCancelledBy(getCurrentUsername());
        data.setCancelledAt(now());
        data.setCancelReason(request.getCancelReason());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());

        return buildEntityToResponse(trxSalesRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PosSalesResponse> searchData(PosSalesSearchRequest request) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        return trxSalesRepository.findAll(TrxSalesSpecification.filter(request), pageable).map(this::buildEntityToResponse);
    }

}
