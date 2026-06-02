package com.epos.backend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.epos.backend.enums.PromoApplyType;
import com.epos.backend.enums.PromoStatus;
import com.epos.backend.exception.ConflictException;
import com.epos.backend.exception.NotFoundException;
import com.epos.backend.model.dto.context.PromoCalculationContext;
import com.epos.backend.model.dto.context.PromoCalculationContext.PromoCalculationItem;
import com.epos.backend.model.dto.request.CreatePromoRequest;
import com.epos.backend.model.dto.request.CreatePromoRequest.PromoRewardRequest;
import com.epos.backend.model.dto.request.CreatePromoRequest.PromoRuleRequest;
import com.epos.backend.model.dto.request.PromoSimulationRequest;
import com.epos.backend.model.dto.request.UpdatePromoRequest;
import com.epos.backend.model.dto.request.search.PromoSearchRequest;
import com.epos.backend.model.dto.response.PromoResponse;
import com.epos.backend.model.dto.response.PromoResponse.PromoRewardResponse;
import com.epos.backend.model.dto.response.PromoResponse.PromoRuleResponse;
import com.epos.backend.model.dto.result.AppliedPromoResult;
import com.epos.backend.model.entity.Promo;
import com.epos.backend.model.entity.PromoReward;
import com.epos.backend.model.entity.PromoRule;
import com.epos.backend.repository.PromoRepository;
import com.epos.backend.service.PromoEngineService;
import com.epos.backend.service.PromoService;
import com.epos.backend.specification.PromoSpecification;
import com.epos.backend.util.ParseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromoServiceImpl extends Services implements PromoService {
    
    private PromoRepository promoRepository;
    private PromoEngineService promoEngineService;

    private void validationRequest(CreatePromoRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Tanggal mulai tidak boleh lebih besar dari tanggal berakhir");
        }

        if (PromoApplyType.MANUAL_CODE.equals(request.getApplyType()) && !StringUtils.hasText(request.getPromoCode())) {
            throw new IllegalArgumentException("Kode promo wajib diisi untuk MANUAL_CODE");
        }
    }

    private void validationPromoCodeCreate(CreatePromoRequest request) {
        if (StringUtils.hasText(request.getPromoCode())
                && promoRepository.existsByPromoCodeIgnoreCaseAndIsDeletedFalse(request.getPromoCode().trim())) {
            throw new ConflictException("Kode promo sudah digunakan");
        }
    }

    private void validationPromoCodeUpdate(Long id, CreatePromoRequest request) {
        if (StringUtils.hasText(request.getPromoCode()) && promoRepository.existsByPromoCodeIgnoreCaseAndIdNotAndIsDeletedFalse(request.getPromoCode().trim(), id)) {
            throw new ConflictException("Kode promo sudah digunakan");
        }
    }

    private void mapRulesAndRewards(Promo promo, List<PromoRuleRequest> ruleRequests, List<PromoRewardRequest> rewardRequests) {
        for (PromoRuleRequest requestDetail : ruleRequests) {
            promo.getRules().add(PromoRule.builder()
                    .promo(promo)
                    .ruleType(requestDetail.getRuleType())
                    .operator(requestDetail.getOperator())
                    .ruleValue(requestDetail.getRuleValue())
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build());
        }

        for (PromoRewardRequest requestDetail : rewardRequests) {
            promo.getRewards().add(PromoReward.builder()
                    .promo(promo)
                    .rewardType(requestDetail.getRewardType())
                    .rewardValue(requestDetail.getRewardValue())
                    .maxDiscountAmount(requestDetail.getMaxDiscountAmount())
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build());
        }
    }

    private PromoResponse buildEntityToResponse(Promo data) {
        return PromoResponse.builder()
                .id(data.getId())
                .promoNo(data.getPromoNo())
                .promoName(data.getPromoName())
                .promoCode(data.getPromoCode())
                .applyType(data.getApplyType())
                .promoType(data.getPromoType())
                .status(data.getStatus())
                .startDate(data.getStartDate())
                .endDate(data.getEndDate())
                .priority(data.getPriority())
                .stackable(data.getStackable())
                .maxUsage(data.getMaxUsage())
                .currentUsage(data.getCurrentUsage())
                .maxUsagePerCustomer(data.getMaxUsagePerCustomer())
                .description(data.getDescription())
                .isActive(data.getIsActive())
                .rules(data.getRules().stream().map(this::buildEntityToResponseRule).toList())
                .rewards(data.getRewards().stream().map(this::buildEntityToResponseReward).toList())
            .build();
    }

    private PromoRuleResponse buildEntityToResponseRule(PromoRule data) {
        return PromoRuleResponse.builder()
                .id(data.getId())
                .ruleType(data.getRuleType())
                .operator(data.getOperator())
                .ruleValue(data.getRuleValue())
            .build();
    }

    private PromoRewardResponse buildEntityToResponseReward(PromoReward data) {
        return PromoRewardResponse.builder()
                .id(data.getId())
                .rewardType(data.getRewardType())
                .rewardValue(data.getRewardValue())
                .maxDiscountAmount(data.getMaxDiscountAmount())
            .build();
    }

    @Transactional
    @Override
    public PromoResponse createPromo(CreatePromoRequest request) {
        validationRequest(request);
        validationPromoCodeCreate(request);

        Promo newData = Promo.builder()
                .promoNo(automationGeneratorServices.generatePromoNo())
                .promoName(request.getPromoName().trim())
                .promoCode(ParseUtil.trimToNullUpper(request.getPromoCode()))
                .applyType(request.getApplyType())
                .promoType(request.getPromoType())
                .status(PromoStatus.ACTIVE)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priority(ParseUtil.defaultLong(request.getPriority()))
                .stackable(Boolean.TRUE.equals(request.getStackable()))
                .maxUsage(request.getMaxUsage())
                .currentUsage(0L)
                .maxUsagePerCustomer(request.getMaxUsagePerCustomer())
                .description(ParseUtil.trimToNull(request.getDescription()))
                .isActive(true)
                .isDeleted(false)
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .rules(new ArrayList<>())
                .rewards(new ArrayList<>())
            .build();

        mapRulesAndRewards(newData, request.getRules(), request.getRewards());
        return buildEntityToResponse(promoRepository.save(newData));
    }

    @Transactional
    @Override
    public PromoResponse updatePromo(Long id, UpdatePromoRequest request) {
        validationRequest(request);
        validationPromoCodeUpdate(id, request);

        Promo data = promoRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Promo tidak ditemukan"));
        data.setPromoName(request.getPromoName().trim());
        data.setPromoCode(ParseUtil.trimToNullUpper(request.getPromoCode()));
        data.setApplyType(request.getApplyType());
        data.setPromoType(request.getPromoType());
        data.setStatus(request.getStatus() == null ? data.getStatus() : request.getStatus());
        data.setStartDate(request.getStartDate());
        data.setEndDate(request.getEndDate());
        data.setPriority(ParseUtil.defaultLong(request.getPriority()));
        data.setStackable(Boolean.TRUE.equals(request.getStackable()));
        data.setMaxUsage(request.getMaxUsage());
        data.setMaxUsagePerCustomer(request.getMaxUsagePerCustomer());
        data.setDescription(ParseUtil.trimToNull(request.getDescription()));
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        data.getRules().clear();
        data.getRewards().clear();

        mapRulesAndRewards(data, request.getRules(), request.getRewards());
        return buildEntityToResponse(promoRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public PromoResponse getDataById(Long id) {
        Promo data = promoRepository.findById(id).filter(promo -> Boolean.FALSE.equals(promo.getIsDeleted())).orElseThrow(() -> new NotFoundException("Promo tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional
    @Override
    public PromoResponse deletePromo(Long id) {
        Promo data = promoRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Promo tidak ditemukan"));
        data.setIsDeleted(true);
        data.setIsActive(false);
        data.setStatus(PromoStatus.INACTIVE);
        data.setDeletedBy(getCurrentUsername());
        data.setDeletedAt(now());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntityToResponse(promoRepository.save(data));
    }

    @Transactional
    @Override
    public PromoResponse activatePromo(Long id) {
        Promo data = promoRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Promo tidak ditemukan"));
        data.setIsActive(true);
        data.setStatus(PromoStatus.ACTIVE);
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntityToResponse(promoRepository.save(data));
    }

    @Transactional
    @Override
    public PromoResponse deactivatePromo(Long id) {
        Promo data = promoRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Promo tidak ditemukan"));
        data.setIsActive(false);
        data.setStatus(PromoStatus.INACTIVE);
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntityToResponse(promoRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public AppliedPromoResult simulate(PromoSimulationRequest request) {
        PromoCalculationContext context = PromoCalculationContext.builder()
                .subtotal(request.getSubtotal())
                .paymentMethod(request.getPaymentMethod())
                .promoCode(request.getPromoCode())
                .items(request.getItems().stream()
                    .map(item -> PromoCalculationItem.builder()
                            .productId(item.getProductId())
                            .categoryId(item.getCategoryId())
                            .qty(item.getQty())
                            .price(item.getPrice())
                            .subtotal(item.getSubtotal())
                        .build())
                    .toList())
            .build();

        if (StringUtils.hasText(request.getPromoCode())) return promoEngineService.calculateManualCodePromo(context, request.getPromoCode());

        return promoEngineService.calculateBestPromo(context);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PromoResponse> getAllActive() {
        return promoRepository.findActivePromos(PromoStatus.ACTIVE, PromoApplyType.AUTO, LocalDateTime.now()).stream().map(this::buildEntityToResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PromoResponse> searchData(PromoSearchRequest request) {
        PromoSearchRequest searchRequest = request == null ? new PromoSearchRequest() : request;
        return promoRepository.findAll(PromoSpecification.search(searchRequest), PageRequest.of(searchRequest.getPage(), searchRequest.getSize())).map(this::buildEntityToResponse);
    }
    
}
