package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.epos.backend.enums.PromoApplyType;
import com.epos.backend.enums.PromoStatus;
import com.epos.backend.model.dto.context.PromoCalculationContext;
import com.epos.backend.model.dto.context.PromoCalculationContext.PromoCalculationItem;
import com.epos.backend.model.dto.result.AppliedPromoResult;
import com.epos.backend.model.entity.Promo;
import com.epos.backend.model.entity.PromoReward;
import com.epos.backend.model.entity.PromoRule;
import com.epos.backend.repository.PromoRepository;
import com.epos.backend.service.PromoEngineService;
import com.epos.backend.util.ParseUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromoEngineServiceImpl extends Services implements PromoEngineService {
    
    private final PromoRepository promoRepository;

    private boolean matchCategoryId(PromoRule data, PromoCalculationContext promoCalculationContext) {
        List<Integer> values = (List<Integer>) data.getRuleValue().get("values");

        Set<Long> allowed = new HashSet<>();
        for (Integer v : values) allowed.add(v.longValue());

        return promoCalculationContext.getItems().stream().anyMatch(item -> allowed.contains(item.getCategoryId()));
    }

    private boolean matchPaymentMethod(PromoRule data, PromoCalculationContext promoCalculationContext) {
        String value = String.valueOf(data.getRuleValue().get("value"));

        if (promoCalculationContext.getPaymentMethod() == null) return false;

        return promoCalculationContext.getPaymentMethod().name().equalsIgnoreCase(value);
    }

    private boolean matchProductId(PromoRule data, PromoCalculationContext promoCalculationContext) {
        List<Integer> values = (List<Integer>) data.getRuleValue().get("values");

        Set<Long> allowed = new HashSet<>();
        for (Integer v : values) allowed.add(v.longValue());

        return promoCalculationContext.getItems().stream().anyMatch(item -> allowed.contains(item.getProductId()));
    }

    private boolean matchMinQty(PromoRule data, PromoCalculationContext promoCalculationContext) {
        Long value = ParseUtil.getLong(data.getRuleValue(), "value");
        Long totalQty = promoCalculationContext.getItems().stream().mapToLong(PromoCalculationItem::getQty).sum();
        return totalQty >= value;
    }
    
    private boolean matchMinTransaction(PromoRule data, PromoCalculationContext promoCalculationContext) {
        BigDecimal value = ParseUtil.getBigDecimal(data.getRuleValue(), "value");
        return promoCalculationContext.getSubtotal().compareTo(value) >= 0;
    }

    private boolean matchRule(PromoRule data, PromoCalculationContext promoCalculationContext) {
        return switch (data.getRuleType()) {
            case MIN_TRANSACTION_AMOUNT -> matchMinTransaction(data, promoCalculationContext);
            case MIN_QTY -> matchMinQty(data, promoCalculationContext);
            case PRODUCT_ID -> matchProductId(data, promoCalculationContext);
            case CATEGORY_ID -> matchCategoryId(data, promoCalculationContext);
            case PAYMENT_METHOD -> matchPaymentMethod(data, promoCalculationContext);
        };
    }

    private boolean isEligible(Promo data, PromoCalculationContext promoCalculationContext) {
        if (data.getMaxUsage() != null && data.getCurrentUsage() >= data.getMaxUsage()) {
            return false;
        }

        for (PromoRule rule : data.getRules()) {
            if (!matchRule(rule, promoCalculationContext)) {
                return false;
            }
        }

        return true;
    }

    private AppliedPromoResult buildDataNoPromo() {
        return AppliedPromoResult.builder()
                .applied(false)
                .discountAmount(BigDecimal.ZERO)
                .build();
    }

    private AppliedPromoResult buildCalculateReward(Promo data, PromoCalculationContext context) {
        BigDecimal discount = BigDecimal.ZERO;

        for (PromoReward dataReward : data.getRewards()) {
            BigDecimal calculated = switch (dataReward.getRewardType()) {
                case PERCENTAGE_DISCOUNT -> context.getSubtotal().multiply(dataReward.getRewardValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                case FIXED_DISCOUNT -> dataReward.getRewardValue();
            };

            if (dataReward.getMaxDiscountAmount() != null && calculated.compareTo(dataReward.getMaxDiscountAmount()) > 0) {
                calculated = dataReward.getMaxDiscountAmount();
            }

            discount = discount.add(calculated);
        }

        if (discount.compareTo(context.getSubtotal()) > 0) {
            discount = context.getSubtotal();
        }

        return AppliedPromoResult.builder()
            .applied(true)
            .promoId(data.getId())
            .promoNo(data.getPromoNo())
            .promoName(data.getPromoName())
            .promoCode(data.getPromoCode())
            .discountAmount(discount)
            .promoSnapshot(buildDataMapSnapshot(data, discount))
            .build();
    }

    private Map<String, Object> buildDataMapSnapshot(Promo data, BigDecimal discount) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("promoId", data.getId());
        snapshot.put("promoNo", data.getPromoNo());
        snapshot.put("promoName", data.getPromoName());
        snapshot.put("promoCode", data.getPromoCode());
        snapshot.put("applyType", data.getApplyType().name());
        snapshot.put("promoType", data.getPromoType().name());
        snapshot.put("discountAmount", discount);
        snapshot.put("priority", data.getPriority());
        snapshot.put("stackable", data.getStackable());
        return snapshot;
    }


    @Override
    public AppliedPromoResult calculateBestPromo(PromoCalculationContext context) {
        List<Promo> promos = promoRepository.findActivePromos(PromoStatus.ACTIVE, PromoApplyType.AUTO, LocalDateTime.now());

        return promos.stream()
                .filter(p -> isEligible(p, context))
                .map(p -> buildCalculateReward(p, context))
                .filter(result -> result.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0)
                .max(Comparator.comparing(AppliedPromoResult::getDiscountAmount))
                .orElse(buildDataNoPromo());
    }

    @Override
    public AppliedPromoResult calculateManualCodePromo(PromoCalculationContext context, String promoCode) {
        if (promoCode == null || promoCode.isBlank()) {
            return buildDataNoPromo();
        }

        Promo data = promoRepository.findByPromoCodeAndStatus(promoCode, PromoStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException("Kode promo tidak ditemukan atau tidak aktif"));
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(data.getStartDate()) || now.isAfter(data.getEndDate())) {
            throw new IllegalArgumentException("Kode promo sudah tidak berlaku");
        }

        if (!PromoApplyType.MANUAL_CODE.equals(data.getApplyType())) {
            throw new IllegalArgumentException("Promo bukan tipe kode manual");
        }

        if (!isEligible(data, context)) {
            throw new IllegalArgumentException("Syarat promo tidak terpenuhi");
        }

        return buildCalculateReward(data, context);
    }

}
