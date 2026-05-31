package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.PurchaseStatus;
import com.epos.backend.model.dto.request.TrxPurchaseCancelRequest;
import com.epos.backend.model.dto.request.TrxPurchaseRequest;
import com.epos.backend.model.dto.request.TrxPurchaseRequest.TrxPurchaseItemRequest;
import com.epos.backend.model.dto.request.search.TrxPurchaseSearchRequest;
import com.epos.backend.model.dto.response.TrxPurchaseResponse;
import com.epos.backend.model.dto.response.TrxPurchaseResponse.TrxPurchaseDetailRespose;
import com.epos.backend.model.entity.Product;
import com.epos.backend.model.entity.Supplier;
import com.epos.backend.model.entity.TrxPurchase;
import com.epos.backend.model.entity.TrxPurchaseDetail;
import com.epos.backend.repository.ProductRepository;
import com.epos.backend.repository.SupplierRepository;
import com.epos.backend.repository.TrxPurchaseRepository;
import com.epos.backend.service.TrxPurchaseService;
import com.epos.backend.service.TrxStockMovementService;
import com.epos.backend.specification.TrxPurchaseSpecification;
import com.epos.backend.util.FunctionUtil;
import com.epos.backend.util.GeneratorUtil;
import com.epos.backend.util.ParseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrxPurchaseServiceImpl extends Services implements TrxPurchaseService {
    
    private final TrxPurchaseRepository trxPurchaseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final TrxStockMovementService trxStockMovementService;

    private void validationPurchase(TrxPurchaseRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Item pembelian tidak boleh kosong");
        }
        
        Set<Long> productIds = new HashSet<>();
        for (TrxPurchaseItemRequest requestItem : request.getItems()) {
            if (!productIds.add(requestItem.getProductId())) {
                throw new RuntimeException("Produk duplikat dalam satu pembelian tidak diperbolehkan");
            }
            if (requestItem.getQty() == null || requestItem.getQty() <= 0) {
                throw new RuntimeException("Kuantiti harus lebih dari 0");
            }
            if (requestItem.getPurchasePrice() == null || requestItem.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Harga beli harus lebih besar dari 0");
            }
        }

        BigDecimal discount = ParseUtil.defaultAmount(request.getDiscountAmount());
        BigDecimal tax = ParseUtil.defaultAmount(request.getTaxAmount());

        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Diskon tidak boleh minus");
        }
        if (tax.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Pajak tidak boleh minus");
        }
    }

    private void validationEditable(TrxPurchase data) {
        if (!PurchaseStatus.DRAFT.equals(data.getStatus())) {
            throw new RuntimeException("Pembelian hanya bisa diubah saat status DRAFT");
        }
    }

    private void validationProduct(Product data) {
        if (Boolean.TRUE.equals(data.getIsDeleted())) {
            throw new RuntimeException("Produk sudah dihapus");
        }
        if (Boolean.FALSE.equals(data.getIsActive())) {
            throw new RuntimeException("Produk tidak aktif: " + data.getName());
        }
    }

    private TrxPurchaseDetailRespose buildEntityDetailToResponse(TrxPurchaseDetail data) {
        return TrxPurchaseDetailRespose.builder()
            .id(data.getId())
            .productId(data.getProduct().getId())
            .productName(data.getProductNameSnapshot())
            .sku(data.getSkuSnapshot())
            .qty(data.getQty())
            .purchasePrice(data.getPurchasePrice())
            .subTotal(data.getSubTotal())
            .build();
    }

    private TrxPurchaseResponse buildEntityToResponse(TrxPurchase data) {
        return TrxPurchaseResponse.builder()
            .id(data.getId())
            .purchaseNo(data.getPurchaseNo())
            .invoiceNo(data.getInvoiceNo())
            .supplierId(data.getSupplier().getId())
            .supplierName(data.getSupplier().getName())
            .purchaseDate(data.getPurchaseDate())
            .status(data.getStatus())
            .subTotalAmount(data.getSubTotalAmount())
            .discountAmount(data.getDiscountAmount())
            .taxAmount(data.getTaxAmount())
            .grandTotalAmount(data.getGrandTotalAmount())
            .notes(data.getNotes())
            .createdBy(data.getCreatedBy())
            .createdAt(data.getCreatedAt())
            .updatedBy(data.getUpdatedBy())
            .updatedAt(data.getUpdatedAt())
            .completedBy(data.getCompletedBy())
            .completedAt(data.getCompletedAt())
            .cancelledBy(data.getCancelledBy())
            .cancelledAt(data.getCancelledAt())
            .cancelReason(data.getCancelReason())
            .items(data.getDetails() != null ? List.of() : data.getDetails().stream().map(this::buildEntityDetailToResponse).toList())
            .build();
    }

    private TrxPurchaseResponse buildEntityToResponseWithoutDetails(TrxPurchase data) {
        return TrxPurchaseResponse.builder()
            .id(data.getId())
            .purchaseNo(data.getPurchaseNo())
            .invoiceNo(data.getInvoiceNo())
            .supplierId(data.getSupplier().getId())
            .supplierName(data.getSupplier().getName())
            .purchaseDate(data.getPurchaseDate())
            .status(data.getStatus())
            .subTotalAmount(data.getSubTotalAmount())
            .discountAmount(data.getDiscountAmount())
            .taxAmount(data.getTaxAmount())
            .grandTotalAmount(data.getGrandTotalAmount())
            .notes(data.getNotes())
            .createdBy(data.getCreatedBy())
            .createdAt(data.getCreatedAt())
            .updatedBy(data.getUpdatedBy())
            .updatedAt(data.getUpdatedAt())
            .completedBy(data.getCompletedBy())
            .completedAt(data.getCompletedAt())
            .cancelledBy(data.getCancelledBy())
            .cancelledAt(data.getCancelledAt())
            .cancelReason(data.getCancelReason())
            .items(List.of())
            .build();
    }

    @Transactional
    @Override
    public TrxPurchaseResponse createPurchase(TrxPurchaseRequest request) {
        validationPurchase(request);

        Supplier dataSupplier = supplierRepository.findByIdAndIsDeletedFalse(request.getSupplierId()).orElseThrow(() -> new RuntimeException("Supplier tidak ditemukan"));
        Long seqInvoice = trxPurchaseRepository.getNextPurchaseInvoiceNoSequence();
        BigDecimal subTotalAmount = BigDecimal.ZERO;

        TrxPurchase newData = TrxPurchase.builder()
            .purchaseNo(automationGeneratorServices.generatePurchaseNo())
            .invoiceNo(GeneratorUtil.generateInvoiceNo(seqInvoice))
            .supplier(dataSupplier)
            .purchaseDate(request.getPurchaseDate())
            .status(PurchaseStatus.DRAFT)
            .discountAmount(ParseUtil.defaultAmount(request.getDiscountAmount()))
            .taxAmount(ParseUtil.defaultAmount(request.getTaxAmount()))
            .notes(ParseUtil.trimToNull(request.getNotes()))
            .isDeleted(false)
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .details(new ArrayList<>())
            .build();

        for (TrxPurchaseItemRequest requestItem : request.getItems()) {
            Product dataProduct = productRepository.findActiveProductForUpdate(requestItem.getProductId()).orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

            validationProduct(dataProduct);

            BigDecimal subTotal = requestItem.getPurchasePrice().multiply(BigDecimal.valueOf(requestItem.getQty()));
            System.out.println("subtotal: " + subTotal);
            TrxPurchaseDetail newDataDetail = TrxPurchaseDetail.builder()
                .purchase(newData)
                .product(dataProduct)
                .productNameSnapshot(dataProduct.getName())
                .skuSnapshot(dataProduct.getSku())
                .qty(requestItem.getQty())
                .purchasePrice(requestItem.getPurchasePrice())
                .subTotal(subTotal)
                .build();

            newData.getDetails().add(newDataDetail);
            subTotalAmount = subTotalAmount.add(subTotal);
        }

        newData.setSubTotalAmount(subTotalAmount);
        newData.setGrandTotalAmount(FunctionUtil.calculateGrandTotal(subTotalAmount, newData.getDiscountAmount(), newData.getTaxAmount()));
        trxPurchaseRepository.save(newData);
        return buildEntityToResponse(newData);
    }

    @Transactional
    @Override
    public TrxPurchaseResponse updatePurchase(Long id, TrxPurchaseRequest request) {
        validationPurchase(request);

        TrxPurchase dataPurchase = trxPurchaseRepository.findDetailById(id).orElseThrow(() -> new RuntimeException("Pembelian tidak ditemukan"));
        validationEditable(dataPurchase);

        Supplier dataSupplier = supplierRepository.findByIdAndIsDeletedFalse(request.getSupplierId()).orElseThrow(() -> new RuntimeException("Supplier tidak ditemukan"));
        BigDecimal subTotalAmount = BigDecimal.ZERO;

        dataPurchase.setSupplier(dataSupplier);
        dataPurchase.setPurchaseDate(request.getPurchaseDate());
        dataPurchase.setDiscountAmount(ParseUtil.defaultAmount(request.getDiscountAmount()));
        dataPurchase.setTaxAmount(ParseUtil.defaultAmount(request.getTaxAmount()));
        dataPurchase.setNotes(ParseUtil.trimToNull(request.getNotes()));
        dataPurchase.setUpdatedBy(getCurrentUsername());
        dataPurchase.setUpdatedAt(now());
        dataPurchase.getDetails().clear();

        for (TrxPurchaseItemRequest requestItem : request.getItems()) {
            Product dataProduct = productRepository.findActiveProductForUpdate(requestItem.getProductId()).orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

            validationProduct(dataProduct);

            BigDecimal subTotal = requestItem.getPurchasePrice().multiply(BigDecimal.valueOf(requestItem.getQty()));
            TrxPurchaseDetail newDataDetail = TrxPurchaseDetail.builder()
                .purchase(dataPurchase)
                .product(dataProduct)
                .productNameSnapshot(dataProduct.getName())
                .skuSnapshot(dataProduct.getSku())
                .qty(requestItem.getQty())
                .purchasePrice(requestItem.getPurchasePrice())
                .subTotal(subTotal)
                .build();

            dataPurchase.getDetails().add(newDataDetail);
            subTotalAmount = subTotalAmount.add(subTotal);
        }

        dataPurchase.setSubTotalAmount(subTotalAmount);
        dataPurchase.setGrandTotalAmount(FunctionUtil.calculateGrandTotal(subTotalAmount, dataPurchase.getDiscountAmount(), dataPurchase.getTaxAmount()));
        trxPurchaseRepository.save(dataPurchase);
        return buildEntityToResponse(dataPurchase);
    }

    @Transactional
    @Override
    public TrxPurchaseResponse completePurchase(Long id) {
        TrxPurchase data = trxPurchaseRepository.findDetailById(id).orElseThrow(() -> new RuntimeException("Pembelian tidak ditemukan"));

        if (!PurchaseStatus.DRAFT.equals(data.getStatus())) {
            throw new RuntimeException("Hanya pembelian DRAFT yang bisa diselesaikan");
        }
        if (data.getDetails() == null || data.getDetails().isEmpty()) {
            throw new RuntimeException("Item pembelian kosong");
        }

        for (TrxPurchaseDetail dataDetail : data.getDetails()) {
            Product dataProduct = productRepository.findActiveProductForUpdate(dataDetail.getProduct().getId()).orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
            validationProduct(dataProduct);

            dataProduct.setPurchasePrice(dataDetail.getPurchasePrice());
            dataProduct.setUpdatedBy(getCurrentUsername());
            dataProduct.setUpdatedAt(now());
            productRepository.save(dataProduct);

            trxStockMovementService.purchaseStockIn(dataProduct.getId(), dataDetail.getQty(), data.getPurchaseNo(), "Otomatis menambahkan stok dari pembelian " + data.getPurchaseNo());
        }

        data.setStatus(PurchaseStatus.COMPLETED);
        data.setCompletedBy(getCurrentUsername());
        data.setCompletedAt(now());
        trxPurchaseRepository.save(data);
        return buildEntityToResponse(data);
    }

    @Override
    public TrxPurchaseResponse cancelPurchase(Long id, TrxPurchaseCancelRequest request) {
        TrxPurchase data = trxPurchaseRepository.findDetailById(id).orElseThrow(() -> new RuntimeException("Pembelian tidak ditemukan"));

        if (PurchaseStatus.COMPLETED.equals(data.getStatus())) {
            throw new RuntimeException("Pembelian yang sudah terselesaikan tidak bisa dibatalkan");
        }

        if (PurchaseStatus.CANCELLED.equals(data.getStatus())) {
            throw new RuntimeException("Pembelian yang sudah dibatalkan");
        }

        data.setStatus(PurchaseStatus.CANCELLED);
        data.setCreatedBy(getCurrentUsername());
        data.setCancelledAt(now());
        data.setCancelReason(request.getCancelReason());
        trxPurchaseRepository.save(data);

        return buildEntityToResponse(data);
    }

    @Override
    public TrxPurchaseResponse getPurchaseById(Long id) {
        TrxPurchase data = trxPurchaseRepository.findDetailById(id).orElseThrow(() -> new RuntimeException("Pembelian tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Override
    public Page<TrxPurchaseResponse> searchData(int page, int size, TrxPurchaseSearchRequest request) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<TrxPurchase> specification = TrxPurchaseSpecification.filter(request);
        return trxPurchaseRepository.findAll(specification, pageable).map(this::buildEntityToResponseWithoutDetails);
    }

}
