package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.RefundStatus;
import com.epos.backend.enums.SalesReturnStatus;
import com.epos.backend.enums.SalesStatus;
import com.epos.backend.enums.StockMovementType;
import com.epos.backend.model.dto.request.TrxSalesReturnCancelRequest;
import com.epos.backend.model.dto.request.TrxSalesReturnRequest;
import com.epos.backend.model.dto.request.TrxSalesReturnRequest.TrxSalesReturnItemRequest;
import com.epos.backend.model.dto.request.search.TrxSalesReturnSearchRequest;
import com.epos.backend.model.dto.response.TrxSalesReturnResponse;
import com.epos.backend.model.dto.response.TrxSalesReturnResponse.TrxSalesReturnDetailResponse;
import com.epos.backend.model.dto.response.TrxSalesReturnResponse.TrxSalesReturnRefundPaymentResponse;
import com.epos.backend.model.entity.Product;
import com.epos.backend.model.entity.TrxSales;
import com.epos.backend.model.entity.TrxSalesDetail;
import com.epos.backend.model.entity.TrxSalesRefundPayment;
import com.epos.backend.model.entity.TrxSalesReturn;
import com.epos.backend.model.entity.TrxSalesReturnDetail;
import com.epos.backend.model.entity.TrxStockMovement;
import com.epos.backend.repository.ProductRepository;
import com.epos.backend.repository.TrxSalesDetailRepository;
import com.epos.backend.repository.TrxSalesRepository;
import com.epos.backend.repository.TrxSalesReturnDetailRepository;
import com.epos.backend.repository.TrxSalesReturnRepository;
import com.epos.backend.repository.TrxStockMovementRepository;
import com.epos.backend.service.TrxSalesReturnService;
import com.epos.backend.specification.TrxSalesReturnSpecification;
import com.epos.backend.util.GeneratorUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrxSalesReturnServiceImpl extends Services implements TrxSalesReturnService {
    
    private final TrxSalesRepository trxSalesRepository;
    private final TrxSalesDetailRepository trxSalesDetailRepository;
    private final TrxSalesReturnRepository trxSalesReturnRepository;
    private final TrxSalesReturnDetailRepository trxSalesReturnDetailRepository;
    private final TrxStockMovementRepository trxStockMovementRepository;
    private final ProductRepository productRepository;

    private TrxSalesReturnResponse buildEntityToResponse(TrxSalesReturn data) {
        return TrxSalesReturnResponse.builder()
            .id(data.getId())
            .returnNo(data.getReturnNo())
            .salesId(data.getSales().getId())
            .salesNo(data.getSalesNo())
            .customerName(data.getCustomerName())
            .totalReturnAmount(data.getTotalReturnAmount())
            .refundAmount(data.getRefundAmount())
            .returnStatus(data.getReturnStatus())
            .refundStatus(data.getRefundStatus())
            .returnReason(data.getReturnReason())
            .createdBy(data.getCreatedBy())
            .createdAt(data.getCreatedAt())
            .cancelledBy(data.getCancelledBy())
            .cancelledAt(data.getCancelledAt())
            .cancelReason(data.getCancelReason())
            .details(data.getDetails().stream().map(d -> buildEntityToResponseDetail(d)).toList())
            .refundPayments(data.getRefundPayments().stream().map(rp -> buildEntityToResponseRefundPayment(rp)).toList())
            .build();
    }

    private TrxSalesReturnDetailResponse buildEntityToResponseDetail(TrxSalesReturnDetail data) {
        return TrxSalesReturnDetailResponse.builder()
            .salesDetailId(data.getSalesDetail().getId())
            .productId(data.getProduct().getId())
            .productName(data.getProductNameSnapshot())
            .price(data.getPriceSnapshot())
            .qtySold(data.getQtySold())
            .qtyReturn(data.getQtyReturn())
            .subtotalReturn(data.getSubtotalReturn())
            .build();
    }

    private TrxSalesReturnRefundPaymentResponse buildEntityToResponseRefundPayment(TrxSalesRefundPayment data) {
        return TrxSalesReturnRefundPaymentResponse.builder()
            .refundMethod(data.getRefundMethod())
            .refundAmount(data.getRefundAmount())
            .refundStatus(data.getRefundStatus())
            .build();
    }

    private void validationRequest(TrxSalesReturnRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Item pengembalian tidak boleh kosong");
        }

        Set<Long> salesDetailIds = new HashSet<>();
        for (TrxSalesReturnItemRequest requestItem : request.getItems()) {
            if (!salesDetailIds.add(requestItem.getSalesDetailId())) {
                throw new IllegalArgumentException("Detail penjualan duplikat dalam request: " + requestItem.getSalesDetailId());
            }
        }
    }

    private void validationSalesCanBeReturned(TrxSales data) {
        if (SalesStatus.CANCELLED.equals(data.getStatus())) {
            throw new IllegalArgumentException("Penjualan yang sudah batalkan tidak bisa direturn");
        }
    }

    private void validationSalesDetailBelongsToSales(TrxSalesDetail dataDetail, TrxSales data) {
        if (!dataDetail.getSales().getId().equals(data.getId())) {
            throw new IllegalArgumentException("Detail penjualan tidak sesuai dengan transaksi penjualan");
        }
    }

    private void validationReturnQty(TrxSalesDetail dataDetail, Long qtyReturn) {
        Long alreadyReturnQty = trxSalesReturnDetailRepository.getTotalReturnedQtyBySalesDetailId(dataDetail.getId());
        Long remainingQty = dataDetail.getQty() - alreadyReturnQty;

        if (qtyReturn > remainingQty) {
            throw new IllegalArgumentException("Kuantiti pengembalian melebihi sisa kuantiti yang bisa dikembalikan. Kuantiti yang laku: " + dataDetail.getQty() + ", sudah dikembalikan: " + alreadyReturnQty + ", sisa: " + remainingQty);
        }
    }

    private void validationRefundRequest(TrxSalesReturnRequest request) {
        if (request.getRefundMethod() == null) {
            throw new IllegalArgumentException("Metode pengembalian wajib diisi jika ada refund");
        }
    }

    @Transactional
    @Override
    public TrxSalesReturnResponse createReturn(TrxSalesReturnRequest request) {
        validationRequest(request);
        TrxSales dataSales = trxSalesRepository.findById(request.getSalesId()).orElseThrow(() -> new EntityNotFoundException("Transaksi Penjualan tidak ditemukan"));
        validationSalesCanBeReturned(dataSales);

        TrxSalesReturn newData = TrxSalesReturn.builder()
            .returnNo(GeneratorUtil.generateReturnNo())
            .sales(dataSales)
            .salesNo(dataSales.getSalesNo())
            .customerName(dataSales.getCustomerName())
            .totalReturnAmount(BigDecimal.ZERO)
            .refundAmount(BigDecimal.ZERO)
            .returnStatus(SalesReturnStatus.APPROVED)
            .refundStatus(RefundStatus.NO_REFUND)
            .returnReason(request.getReturnReason())
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .details(new ArrayList<>())
            .refundPayments(new ArrayList<>())
            .build();

        BigDecimal totalReturnAmount = BigDecimal.ZERO;
        for (TrxSalesReturnItemRequest requestItem : request.getItems()) {
            TrxSalesDetail dataSalesDetail = trxSalesDetailRepository.findById(requestItem.getSalesDetailId()).orElseThrow(() -> new EntityNotFoundException("Detail Penjualan tidak ditemukan: " + requestItem.getSalesDetailId()));
            validationSalesDetailBelongsToSales(dataSalesDetail, dataSales);
            validationReturnQty(dataSalesDetail, requestItem.getQtyReturn());

            Product dataProduct = productRepository.findActiveProductForUpdate(dataSalesDetail.getProduct().getId()).orElseThrow(() -> new EntityNotFoundException("Produk tidak ditemukan"));
            Long qtyBefore = dataProduct.getStock();
            Long qtyChange = requestItem.getQtyReturn();
            Long qtyAfter = qtyBefore + qtyChange;

            dataProduct.setStock(qtyAfter);
            dataProduct.setUpdatedBy(getCurrentUsername());
            dataProduct.setUpdatedAt(now());
            productRepository.save(dataProduct);

            BigDecimal subtotalReturn = dataSalesDetail.getPriceSnapshot().multiply(BigDecimal.valueOf(requestItem.getQtyReturn()));

            TrxSalesReturnDetail newDataDetail = TrxSalesReturnDetail.builder()
                .salesReturn(newData)
                .salesDetail(dataSalesDetail)
                .product(dataProduct)
                .productNameSnapshot(dataSalesDetail.getProductNameSnapshot())
                .priceSnapshot(dataSalesDetail.getPriceSnapshot())
                .qtySold(dataSalesDetail.getQty())
                .qtyReturn(requestItem.getQtyReturn())
                .subtotalReturn(subtotalReturn)
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build();

            newData.getDetails().add(newDataDetail);

            TrxStockMovement newDataStockMovement = TrxStockMovement.builder()
                .product(dataProduct)
                .movementType(StockMovementType.SALES_RETURN_IN)
                .qtyBefore(qtyBefore)
                .qtyChange(qtyChange)
                .qtyAfter(qtyAfter)
                .referenceNo(newData.getReturnNo())
                .externalReferenceNo(dataSales.getSalesNo())
                .notes("Sales Return from Sales No " + dataSales.getSalesNo())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build();
            trxStockMovementRepository.save(newDataStockMovement);

            totalReturnAmount = totalReturnAmount.add(subtotalReturn);
        }

        newData.setTotalReturnAmount(totalReturnAmount);
        if (Boolean.TRUE.equals(request.getRefund())) {
            validationRefundRequest(request);

            newData.setRefundAmount(totalReturnAmount);
            newData.setRefundStatus(RefundStatus.REFUNDED);

            TrxSalesRefundPayment newDataRefundPayment = TrxSalesRefundPayment.builder()
                .salesReturn(newData)
                .refundMethod(request.getRefundMethod())
                .refundAmount(totalReturnAmount)
                .refundStatus(RefundStatus.REFUNDED)
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build();
            newData.getRefundPayments().add(newDataRefundPayment);
        } else {
            newData.setRefundAmount(BigDecimal.ZERO);
            newData.setRefundStatus(RefundStatus.NO_REFUND);
        }

        TrxSalesReturn dataSaved = trxSalesReturnRepository.save(newData);
        return buildEntityToResponse(dataSaved);
    }

    @Transactional(readOnly = true)
    @Override
    public TrxSalesReturnResponse getDataById(Long id) {
        TrxSalesReturn data = trxSalesReturnRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pengembelian penjualan tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional(readOnly = true)
    @Override
    public TrxSalesReturnResponse getByReturnNo(String returnNo) {
        TrxSalesReturn data = trxSalesReturnRepository.findByReturnNo(returnNo).orElseThrow(() -> new EntityNotFoundException("Pengembelian penjualan tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional
    @Override
    public TrxSalesReturnResponse cancelReturn(Long id, TrxSalesReturnCancelRequest request) {
        TrxSalesReturn data = trxSalesReturnRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pengembelian penjualan tidak ditemukan"));
        if (SalesReturnStatus.CANCELLED.equals(data.getReturnStatus())) {
            throw new IllegalArgumentException("Pengembalian penjualan sudah dibatalkan");
        }

        for (TrxSalesReturnDetail dataDetail : data.getDetails()) {
            Product dataProduct = productRepository.findActiveProductForUpdate(dataDetail.getProduct().getId()).orElseThrow(() -> new EntityNotFoundException("Produk tidak ditemukan")); 
            Long qtyBefore = dataProduct.getStock();
            Long qtyChange = dataDetail.getQtyReturn() * -1;
            Long qtyAfter = qtyBefore + qtyChange;

            if (qtyAfter < 0) {
                throw new IllegalArgumentException("Stok produk tidak mencukupi untuk membatalkan pengembalian: " + dataDetail.getProductNameSnapshot());
            }

            dataProduct.setStock(qtyAfter);
            dataProduct.setUpdatedBy(getCurrentUsername());
            dataProduct.setUpdatedAt(now());
            productRepository.save(dataProduct);

            TrxStockMovement newDataStockMovement = TrxStockMovement.builder()
                .product(dataProduct)
                .movementType(StockMovementType.SALES_RETURN_CANCEL_OUT)
                .qtyBefore(qtyBefore)
                .qtyChange(qtyChange)
                .qtyAfter(qtyAfter)
                .referenceNo(data.getReturnNo())
                .externalReferenceNo(data.getSalesNo())
                .notes("Cancel Sales Return. Reason: " + request.getCancelReason())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build();
            trxStockMovementRepository.save(newDataStockMovement);
        }

        data.setReturnStatus(SalesReturnStatus.CANCELLED);
        data.setCancelledBy(getCurrentUsername());
        data.setCancelledAt(now());
        data.setCancelReason(request.getCancelReason());

        if (RefundStatus.REFUNDED.equals(data.getRefundStatus())) {
            data.setRefundStatus(RefundStatus.REFUND_CANCELLED);
            
            for (TrxSalesRefundPayment dataRefundPayment : data.getRefundPayments()) {
                dataRefundPayment.setRefundStatus(RefundStatus.REFUND_CANCELLED);
            }
        }

        TrxSalesReturn dataSaved = trxSalesReturnRepository.save(data);
        return buildEntityToResponse(dataSaved);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TrxSalesReturnResponse> searchData(Pageable pageable, TrxSalesReturnSearchRequest request) {
        return trxSalesReturnRepository.findAll(TrxSalesReturnSpecification.filter(request), pageable).map(this::buildEntityToResponse);
    }

}
