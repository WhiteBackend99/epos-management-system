package com.epos.backend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.PromoApplyType;
import com.epos.backend.enums.PromoStatus;
import com.epos.backend.model.dto.context.PromoCalculationContext;
import com.epos.backend.model.dto.context.PromoCalculationContext.PromoCalculationItem;
import com.epos.backend.model.dto.request.CreatePromoRequest;
import com.epos.backend.model.dto.request.CreatePromoRequest.PromoRewardRequest;
import com.epos.backend.model.dto.request.CreatePromoRequest.PromoRuleRequest;
import com.epos.backend.model.dto.request.PromoSimulationRequest;
import com.epos.backend.model.dto.request.UpdatePromoRequest;
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
import com.epos.backend.util.GeneratorUtil;
import com.epos.backend.util.ParseUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromoServiceImpl extends Services implements PromoService {
    
    private final PromoRepository promoRepository;
    private final PromoEngineService promoEngineService;

    private void validationRequest(CreatePromoRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Tanggal dimulai tidak boleh lebih besar dari tanggal berakhir");
        }

        if (PromoApplyType.MANUAL_CODE.equals(request.getApplyType()) && (request.getPromoCode() == null || request.getPromoCode().isBlank())) {
            throw new IllegalArgumentException("Kode promo wajib diisi untuk MANUAL_CODE");
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
            .rules(data.getRules().stream()
                    .map(rule -> buildEntityToResponseRule(rule))
                    .toList())
            .rewards(data.getRewards().stream()
                    .map(reward -> buildEntityToResponseReward(reward))
                    .toList())
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

        Promo newData = Promo.builder()
            .promoNo(GeneratorUtil.generatePromoNo())
            .promoName(request.getPromoName())
            .promoCode(request.getPromoCode())
            .applyType(request.getApplyType())
            .promoType(request.getPromoType())
            .status(PromoStatus.ACTIVE)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .priority(ParseUtil.defaultLong(request.getPriority()))
            .stackable(Boolean.TRUE.equals(request.getStackable()))
            .maxUsage(request.getMaxUsage())
            .currentUsage(0l)
            .maxUsagePerCustomer(request.getMaxUsagePerCustomer())
            .description(request.getDescription())
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .rules(new ArrayList<>())
            .rewards(new ArrayList<>())
            .build();

        for (PromoRuleRequest requestDetail : request.getRules()) {
            newData.getRules().add(PromoRule.builder()
                .promo(newData)
                .ruleType(requestDetail.getRuleType())
                .operator(requestDetail.getOperator())
                .ruleValue(requestDetail.getRuleValue())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build());
        }

        for (PromoRewardRequest requestDetail : request.getRewards()) {
            newData.getRewards().add(PromoReward.builder()
                .promo(newData)
                .rewardType(requestDetail.getRewardType())
                .rewardValue(requestDetail.getRewardValue())
                .maxDiscountAmount(requestDetail.getMaxDiscountAmount())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build());
        }

        return buildEntityToResponse(promoRepository.save(newData));
    }

    @Transactional
    @Override
    public PromoResponse updatePromo(Long id, UpdatePromoRequest request) {
        validationRequest(request);

        Promo data = promoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Promo tidak ditemukan"));

        data.setPromoName(request.getPromoName());
        data.setPromoCode(request.getPromoCode());
        data.setApplyType(request.getApplyType());
        data.setPromoType(request.getPromoType());
        data.setStatus(request.getStatus() == null ? data.getStatus() : request.getStatus());
        data.setStartDate(request.getStartDate());
        data.setEndDate(request.getEndDate());
        data.setPriority(ParseUtil.defaultLong(request.getPriority()));
        data.setStackable(Boolean.TRUE.equals(request.getStackable()));
        data.setMaxUsage(request.getMaxUsage());
        data.setMaxUsagePerCustomer(request.getMaxUsagePerCustomer());
        data.setDescription(request.getDescription());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        data.getRules().clear();
        data.getRewards().clear();

        for (PromoRuleRequest requestDetail : request.getRules()) {
            data.getRules().add(PromoRule.builder()
                .promo(data)
                .ruleType(requestDetail.getRuleType())
                .operator(requestDetail.getOperator())
                .ruleValue(requestDetail.getRuleValue())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build());
        }

        for (PromoRewardRequest requestDetail : request.getRewards()) {
            data.getRewards().add(PromoReward.builder()
                .promo(data)
                .rewardType(requestDetail.getRewardType())
                .rewardValue(requestDetail.getRewardValue())
                .maxDiscountAmount(requestDetail.getMaxDiscountAmount())
                .createdBy(getCurrentUsername())
                .createdAt(now())
                .build());
        }

        return buildEntityToResponse(promoRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public PromoResponse getDataById(Long id) {
        return promoRepository.findById(id).map(this::buildEntityToResponse).orElseThrow(() -> new EntityNotFoundException("Promo tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PromoResponse> getAllActive() {
        return promoRepository.findActivePromos(PromoStatus.ACTIVE, PromoApplyType.AUTO, LocalDateTime.now()).stream().map(this::buildEntityToResponse).toList();
    }

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

        if (request.getPromoCode() != null && !request.getPromoCode().isBlank()) {
            return promoEngineService.calculateManualCodePromo(context, request.getPromoCode());
        }

        return promoEngineService.calculateBestPromo(context);
    }

}
