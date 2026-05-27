package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.CashierShiftStatus;
import com.epos.backend.enums.PaymentStatus;
import com.epos.backend.enums.SalesStatus;
import com.epos.backend.enums.StockMovementType;
import com.epos.backend.model.dto.context.PromoCalculationContext;
import com.epos.backend.model.dto.context.PromoCalculationContext.PromoCalculationItem;
import com.epos.backend.model.dto.request.PosSalesCancelRequest;
import com.epos.backend.model.dto.request.PosSalesRequest;
import com.epos.backend.model.dto.request.PosSalesRequest.PosSalesItemRequest;
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
import com.epos.backend.util.FunctionUtil;
import com.epos.backend.util.GeneratorUtil;
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
        List<PosSalesDetailResponse> details = data.getDetails().stream().map(dtl -> buildEntityToResponseDetail(dtl)).toList();
        List<PosSalesPaymentResponse> payments = data.getPayments().stream().map(p -> buildEntityToResponsePayment(p)).toList();
        
        return PosSalesResponse.builder()
            .id(data.getId())
            .salesNo(data.getSalesNo())
            .customerName(data.getCustomerName())
            .subtotal(data.getSubtotal())
            .discountAmount(data.getDiscountAmount())
            .taxAmount(data.getTaxAmount())
            .grandTotal(data.getGrandTotal())
            .paidAmount(data.getPaidAmount())
            .changeAmount(data.getChangeAmount())
            .status(data.getStatus())
            .createdBy(data.getCreatedBy())
            .createdAt(data.getCreatedAt())
            .cancelledBy(data.getCancelledBy())
            .cancelledAt(data.getCancelledAt())
            .cancelReason(data.getCancelReason())
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

    private void validationRequest(PosSalesRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Item penjualan tidak boleh kosong");
        }
        if (request.getPayments() == null || request.getPayments().isEmpty()) {
            throw new IllegalArgumentException("Pembayaran tidak boleh kosong");
        }

        Set<Long> productIds = new HashSet<>();
        for (PosSalesItemRequest requestItem : request.getItems()) {
            if (!productIds.add(requestItem.getProductId())) {
                throw new IllegalArgumentException("Produk duplikat dalam transaksi: " + requestItem.getProductId());
            }
        }
    }

    private void validationProductCanBeSold(Product data, Long qty) {
        if (data.getSellingPrice() == null || data.getSellingPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Harga jual produk belum valid: " + data.getName());
        }
        if (data.getStock() == null || data.getStock() < qty) {
            throw new IllegalArgumentException("Stok produk " + data.getName() + " tidak mencukupi");
        }
    }

    private void validationGrandTotal(BigDecimal grandTotal) {
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Grand total tidak boleh kurang dari 0");
        }
    }

    @Transactional
    @Override
    public PosSalesResponse createSales(PosSalesRequest request) {
        TrxCashierShift dataCashierShift = trxCashierShiftRepository.findFirstByCashierUsernameAndStatusOrderByOpenedAtDesc(getCurrentUsername(), CashierShiftStatus.OPEN).orElseThrow(() -> new IllegalArgumentException("Kasir belum membuka shift"));
        
        validationRequest(request);

        String salesNo = GeneratorUtil.generateSalesNo();
        List<TrxSalesDetail> salesDetails = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        TrxSales newData = TrxSales.builder()
            .salesNo(salesNo)
            .customerName(request.getCustomerName())
            .subtotal(BigDecimal.ZERO)
            .discountAmount(ParseUtil.defaultAmount(request.getDiscountAmount()))
            .taxAmount(ParseUtil.defaultAmount(request.getTaxAmount()))
            .grandTotal(BigDecimal.ZERO)
            .paidAmount(BigDecimal.ZERO)
            .changeAmount(BigDecimal.ZERO)
            .status(SalesStatus.SUCCESS)
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .cashierShift(dataCashierShift)
            .pointEarned(0l)
            .pointRedeemed(0l)
            .pointDiscountAmount(BigDecimal.ZERO)
            .loyaltyProcessedFlag(false)
            .details(new ArrayList<>())
            .payments(new ArrayList<>())
            .build();

        if (request.getCustomerMemberId() != null) {
            CustomerMember dataMember = customerMemberRepository.findById(request.getCustomerMemberId()).orElseThrow(() -> new RuntimeException("Member tidak ditemukan"));
            newData.setCustomerMember(dataMember);
            newData.setMemberCodeSnapshot(dataMember.getMemberCode());
            newData.setMemberNameSnapshot(dataMember.getFullName());
        }

        for (PosSalesItemRequest requestItem : request.getItems()) {
            Product dataProduct = productRepository.findById(requestItem.getProductId()).orElseThrow(() -> new EntityNotFoundException("Produk tidak ditemukan: " + requestItem.getProductId()));
            validationProductCanBeSold(dataProduct, requestItem.getQty());

            Long qtyBefore = dataProduct.getStock();
            Long qtyChange = requestItem.getQty() * -1;
            Long qtyAfter = qtyBefore + qtyChange;

            BigDecimal price = dataProduct.getSellingPrice();
            BigDecimal itemSubtotal = price.multiply(BigDecimal.valueOf(requestItem.getQty()));

            dataProduct.setStock(dataProduct.getStock() - requestItem.getQty());
            productRepository.save(dataProduct);

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

            dataProduct.setStock(qtyAfter);
            productRepository.save(dataProduct);

            TrxStockMovement newDataStockMovement = TrxStockMovement.builder()
                .product(dataProduct)
                .movementType(StockMovementType.SALES_OUT)
                .qtyBefore(qtyBefore)
                .qtyChange(qtyChange)
                .qtyAfter(qtyAfter)
                .referenceNo(salesNo)
                .notes("POS sales transaction " + salesNo)
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .externalReferenceNo(null)
                .build();
            trxStockMovementRepository.save(newDataStockMovement);
        }

        /* SECTION CALCULATION PROMO*/
        PromoCalculationContext promoContext = PromoCalculationContext.builder()
            .subtotal(subtotal)
            .paymentMethod(request.getPayments().get(0).getPaymentMethod())
            .promoCode(request.getPromoCode())
            .customerName(request.getCustomerName())
            .items(newData.getDetails().stream()
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

        BigDecimal promoDiscount = promoResult.getDiscountAmount();
        BigDecimal manualDiscount = ParseUtil.defaultAmount(request.getDiscountAmount());
        BigDecimal totalDiscount = manualDiscount.add(promoDiscount);
        BigDecimal tax = ParseUtil.defaultAmount(request.getTaxAmount());
        BigDecimal grandTotal = subtotal.subtract(totalDiscount).add(tax);

        validationGrandTotal(grandTotal);
        
        BigDecimal paidAmount = FunctionUtil.calculatePaidAmount(request.getPayments());
        if (paidAmount.compareTo(grandTotal) < 0) {
            throw new IllegalArgumentException("Nominal pembayaran kurang dari grand total");
        }

        BigDecimal changeAmount = paidAmount.subtract(grandTotal);
        List<TrxSalesPayment> payments = request.getPayments().stream()
            .map(p -> TrxSalesPayment.builder()
                .sales(newData)
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(PaymentStatus.PAID)
                .paidAmount(p.getPaidAmount())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build())
            .toList();

        newData.setSubtotal(subtotal);
        newData.setPromoDiscountAmount(promoDiscount);
        newData.setDiscountAmount(totalDiscount);
        newData.setTaxAmount(tax);
        newData.setGrandTotal(grandTotal);
        newData.setPaidAmount(paidAmount);
        newData.setChangeAmount(changeAmount);
        newData.setDetails(salesDetails);
        newData.setPayments(payments);

        TrxSales dataSales = trxSalesRepository.save(newData);

        trxLoyaltyPointService.processEarnPoint(newData);

        if (Boolean.TRUE.equals(promoResult.getApplied())) {
            TrxSalesPromo newDataSalesPromo = TrxSalesPromo.builder()
                .salesId(newData.getId())
                .salesNo(newData.getSalesNo())
                .promoId(promoResult.getPromoId())
                .promoNo(promoResult.getPromoNo())
                .promoName(promoResult.getPromoName())
                .promoCode(promoResult.getPromoCode())
                .discountAmount(promoResult.getDiscountAmount())
                .promoSnapshot(promoResult.getPromoSnapshot())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build();
            trxSalesPromoRepository.save(newDataSalesPromo);

            Promo dataPromo = promoRepository.findByIdForUpdate(promoResult.getPromoId()).orElseThrow();
            dataPromo.setCurrentUsage(dataPromo.getCurrentUsage() + 1);
            dataPromo.setUpdatedBy(getCurrentUsername());
            dataPromo.setUpdatedAt(now());
            promoRepository.save(dataPromo);
        }

        return buildEntityToResponse(dataSales);
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
        TrxSales data = trxSalesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transaksi penjualan tidak ditemukan"));

        if (SalesStatus.CANCELLED.equals(data.getStatus())) {
            throw new IllegalArgumentException("Transaksi sudah dibatalkan");
        }
        
    for (TrxSalesDetail dataDetail : data.getDetails()) {
            Product dataProduct = dataDetail.getProduct();

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

        trxLoyaltyPointService.reverseEarnPoint(data.getSalesNo());
        trxLoyaltyPointService.reverseRedeemPoint(data.getSalesNo());

        data.setStatus(SalesStatus.CANCELLED);
        data.setCancelledBy(getCurrentUsername());
        data.setCancelledAt(now());
        data.setCancelReason(request.getCancelReason());
        
        TrxSales dataSave = trxSalesRepository.save(data);
        return buildEntityToResponse(dataSave);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PosSalesResponse> searchData(String keyword, Pageable pageable) {
        String search = keyword == null ? "" : keyword;
        return trxSalesRepository.findBySalesNoContainingIgnoreCaseOrCustomerNameContainingIgnoreCase(search, search, pageable).map(this::buildEntityToResponse);
    }

}
