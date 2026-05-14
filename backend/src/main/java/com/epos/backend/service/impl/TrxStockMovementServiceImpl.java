package com.epos.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.StockMovementType;
import com.epos.backend.model.dto.request.TrxStockAdjustmentRequest;
import com.epos.backend.model.dto.request.TrxStockInRequest;
import com.epos.backend.model.dto.request.TrxStockOutRequest;
import com.epos.backend.model.dto.response.TrxStockMovementResponse;
import com.epos.backend.model.entity.Product;
import com.epos.backend.model.entity.TrxStockMovement;
import com.epos.backend.repository.ProductRepository;
import com.epos.backend.repository.TrxStockMovementRepository;
import com.epos.backend.service.TrxStockMovementService;
import com.epos.backend.specification.TrxStockMovementSpecification;
import com.epos.backend.util.GeneratorUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrxStockMovementServiceImpl extends Services implements TrxStockMovementService {
    
    private final ProductRepository productRepository;
    private final TrxStockMovementRepository trxStockMovementRepository;

    private TrxStockMovementResponse buildEntityToResponse(TrxStockMovement data) {
        return TrxStockMovementResponse.builder()
            .id(data.getId())
            .productId(data.getProduct().getId())
            .productName(data.getProduct().getName())
            .categoryId(data.getProduct().getCategory().getId())
            .categoryName(data.getProduct().getCategory().getName())
            .movementType(data.getMovementType())
            .qtyBefore(data.getQtyBefore())
            .qtyChange(data.getQtyChange())
            .qtyAfter(data.getQtyAfter())
            .referenceNo(data.getReferenceNo())
            .notes(data.getNotes())
            .createdBy(data.getCreatedBy())
            .createdAt(data.getCreatedAt())
            .build();
    }

    private Product sectionGetDataProductForUpdate(Long productId) {
        Product dataProduct = productRepository.findActiveProductForUpdate(productId).orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
        if (Boolean.FALSE.equals(dataProduct.getIsActive())) {
            throw new RuntimeException("Produk tidak aktif");
        }
        return dataProduct;
    }

    private TrxStockMovement sectionSaveDataStockMovement(Product dataProduct, StockMovementType movementType, Long qtyBefore, Long qtyChange, Long qtyAfter, String referenceNo, String externalReferenceNo, String notes) {
        TrxStockMovement data = TrxStockMovement.builder()
                                    .product(dataProduct)
                                    .movementType(movementType)
                                    .qtyBefore(qtyBefore)
                                    .qtyChange(qtyChange)
                                    .qtyAfter(qtyAfter)
                                    .referenceNo(referenceNo)
                                    .externalReferenceNo(externalReferenceNo)
                                    .notes(notes)
                                    .createdAt(now())
                                    .createdBy(getCurrentUsername())
                                    .build();
        return trxStockMovementRepository.save(data);
    }

    private Long normalizeStock(Long stock) {
        return stock == null ? 0l : stock;
    }

    @Transactional
    @Override
    public TrxStockMovementResponse stockIn(TrxStockInRequest request) {
        Product dataProduct = sectionGetDataProductForUpdate(request.getProductId());
        Long qtyBefore = normalizeStock(dataProduct.getStock().longValue());
        Long qtyChange = request.getQty();
        Long qtyAfter = qtyBefore + qtyChange;
        String referenceNo = GeneratorUtil.generateReferenceNoStockIn();

        dataProduct.setStock(qtyAfter);
        dataProduct.setUpdatedBy(getCurrentUsername());
        dataProduct.setUpdatedAt(now());
        productRepository.save(dataProduct);

        TrxStockMovement dataStockMovement = sectionSaveDataStockMovement(dataProduct, StockMovementType.IN, qtyBefore, qtyChange, qtyAfter, referenceNo, request.getExternalReferenceNo(), request.getNotes());
        return buildEntityToResponse(dataStockMovement);
    }

    @Transactional
    @Override
    public TrxStockMovementResponse stockOut(TrxStockOutRequest request) {
        Product dataProduct = sectionGetDataProductForUpdate(request.getProductId());
        Long qtyBefore = normalizeStock(dataProduct.getStock().longValue());
        Long qtyChange = request.getQty();

        if (qtyBefore < qtyChange) {
            throw new RuntimeException("Stok tidak mencukupi. Stok tersedia: " + qtyBefore);
        }

        Long qtyAfter = qtyBefore - qtyChange;
        String referenceNo = GeneratorUtil.generateReferenceNoStockOut();

        dataProduct.setStock(qtyAfter);
        dataProduct.setUpdatedBy(getCurrentUsername());
        dataProduct.setUpdatedAt(now());
        productRepository.save(dataProduct);

        TrxStockMovement dataStockMovement = sectionSaveDataStockMovement(dataProduct, StockMovementType.OUT, qtyBefore, qtyChange * -1, qtyAfter, referenceNo, request.getExternalReferenceNo(), request.getNotes());
        return buildEntityToResponse(dataStockMovement);
    }

    @Transactional
    @Override
    public TrxStockMovementResponse adjustment(TrxStockAdjustmentRequest request) {
        Product dataProduct = sectionGetDataProductForUpdate(request.getProductId());
        Long qtyBefore = normalizeStock(dataProduct.getStock().longValue());
        Long qtyAfter = request.getNewStock();
        Long qtyChange = qtyAfter - qtyBefore;
        String referenceNo = GeneratorUtil.generateReferenceNoStockAdjustment();

        dataProduct.setStock(qtyAfter);
        dataProduct.setUpdatedBy(getCurrentUsername());
        dataProduct.setUpdatedAt(now());
        productRepository.save(dataProduct);

        TrxStockMovement dataStockMovement = sectionSaveDataStockMovement(dataProduct, StockMovementType.ADJUSTMENT, qtyBefore, qtyChange, qtyAfter, referenceNo, request.getExternalReferenceNo(), request.getNotes());
        return buildEntityToResponse(dataStockMovement);
    }

    @Override
    public TrxStockMovementResponse getDataById(Long id) {
        TrxStockMovement data = trxStockMovementRepository.findById(id).orElseThrow(() -> new RuntimeException("Data perpindahan stok tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Override
    public Page<TrxStockMovementResponse> searchData(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<TrxStockMovement> specification = Specification.where(TrxStockMovementSpecification.search(search));
        return trxStockMovementRepository.findAll(specification, pageable).map(this::buildEntityToResponse);
    }

}
