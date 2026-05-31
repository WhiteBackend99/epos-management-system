package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.LoyaltyPointType;
import com.epos.backend.enums.MemberStatus;
import com.epos.backend.enums.SalesStatus;
import com.epos.backend.model.dto.request.RedeemPointRequest;
import com.epos.backend.model.dto.response.RedeemPointResponse;
import com.epos.backend.model.dto.result.FormulaCalculationResult;
import com.epos.backend.model.entity.CustomerMember;
import com.epos.backend.model.entity.LoyaltyRule;
import com.epos.backend.model.entity.TrxLoyaltyPointBucket;
import com.epos.backend.model.entity.TrxLoyaltyPointLedger;
import com.epos.backend.model.entity.TrxLoyaltyPointRedemptionDetail;
import com.epos.backend.model.entity.TrxSales;
import com.epos.backend.repository.CustomerMemberRepository;
import com.epos.backend.repository.LoyaltyRuleRepository;
import com.epos.backend.repository.TrxLoyaltyPointBucketRepository;
import com.epos.backend.repository.TrxLoyaltyPointLedgerRepository;
import com.epos.backend.repository.TrxLoyaltyPointRedemptionDetailRepository;
import com.epos.backend.repository.TrxSalesRepository;
import com.epos.backend.service.FormulaEngineService;
import com.epos.backend.service.TrxLoyaltyPointService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrxLoyaltyPointServiceImpl extends Services implements TrxLoyaltyPointService {
    
    private final CustomerMemberRepository customerMemberRepository;
    private final LoyaltyRuleRepository loyaltyRuleRepository;
    private final TrxLoyaltyPointLedgerRepository trxLoyaltyPointLedgerRepository;
    private final TrxLoyaltyPointBucketRepository trxLoyaltyPointBucketRepository;
    private final TrxLoyaltyPointRedemptionDetailRepository trxLoyaltyPointRedemptionDetailRepository;
    private final TrxSalesRepository trxSalesRepository;
    private final FormulaEngineService formulaEngineService;

    private void validationDataMember(CustomerMember data) {
        if (data.getMemberStatus() == MemberStatus.INACTIVE) {
            throw new IllegalArgumentException("Member tidak aktif");
        }

        if (data.getMemberStatus() == MemberStatus.SUSPENDED) {
            throw new IllegalArgumentException("Member sedang ditangguhkan");
        }

        if (data.getMemberStatus() == MemberStatus.BLOCKED) {
            throw new IllegalArgumentException("Member sedang diblokir");
        }
    }

    @Transactional
    @Override
    public RedeemPointResponse redeemPoint(RedeemPointRequest request) {
        TrxSales dataSales = trxSalesRepository.findBySalesNoForUpdate(request.getSalesNo()).orElseThrow(() -> new IllegalArgumentException("Transaksi sales tidak ditemukan"));
        
        if (dataSales.getPointRedeemed() != null && dataSales.getPointRedeemed() > 0) throw new IllegalArgumentException("Transaksi ini sudah pernah melakukan redeem point");
        if (!SalesStatus.DRAFT.equals(dataSales.getStatus())) throw new IllegalArgumentException("Redeem point hanya dapat dilakukan pada transaksi DRAFT");

        CustomerMember dataMember = customerMemberRepository.findByMemberCodeForUpdate(request.getMemberCode()).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));

        validationDataMember(dataMember);

        if (dataMember.getTotalPoint() < request.getRedeemPoint()) throw new IllegalArgumentException("Saldo point tidak mencukupi");

        LoyaltyRule dataLoyaltyRule = loyaltyRuleRepository.findActiveRule().orElseThrow(() -> new IllegalArgumentException("Rule loyalty aktif tidak ditemukan"));

        Map<String, BigDecimal> variables = new HashMap<>();
        variables.put("REDEEM_POINT", BigDecimal.valueOf(request.getRedeemPoint()));
        variables.put("POINT_VALUE_AMOUNT", dataLoyaltyRule.getPointValueAmount());
        FormulaCalculationResult formulaResult = formulaEngineService.evaluate("LOYALTY_REDEEM_AMOUNT",variables);

        BigDecimal redeemAmount = formulaResult.getResult().setScale(2, RoundingMode.DOWN);
        BigDecimal grandTotal = dataSales.getGrandTotal();
        BigDecimal maxRedeemAmount = grandTotal.multiply(dataLoyaltyRule.getMaxRedeemPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);

        if (redeemAmount.compareTo(maxRedeemAmount) > 0) throw new IllegalArgumentException("Nominal redeem melebihi batas maksimal transaksi");

        long beforeBalance = dataMember.getTotalPoint();
        long afterBalance = beforeBalance - request.getRedeemPoint();
        String referenceNo = dataSales.getSalesNo();

        TrxLoyaltyPointLedger newDataPointLedger = trxLoyaltyPointLedgerRepository.save(
            TrxLoyaltyPointLedger.builder()
                    .customerMember(dataMember)
                    .salesNo(dataSales.getSalesNo())
                    .referenceNo(referenceNo)
                    .pointType(LoyaltyPointType.REDEEM)
                    .pointAmount(request.getRedeemPoint() * -1)
                    .balanceBefore(beforeBalance)
                    .balanceAfter(afterBalance)
                    .description("Redeem point untuk sales " + dataSales.getSalesNo())
                    .formulaId(formulaResult.getFormulaId())
                    .formulaVersionId(formulaResult.getFormulaVersionId())
                    .formulaExpressionSnapshot(formulaResult.getExpression())
                    .formulaContextSnapshot(formulaResult.getContext())
                    .formulaResultSnapshot(formulaResult.getResult())
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build()
        );

        long remainingRedeem = request.getRedeemPoint();
        List<TrxLoyaltyPointBucket> buckets = trxLoyaltyPointBucketRepository.findActiveBucketsForRedeem(dataMember.getId());
        for (TrxLoyaltyPointBucket bucket : buckets) {
            if (remainingRedeem <= 0) break;

            long usedPoint = Math.min(bucket.getRemainingPoint(), remainingRedeem);

            bucket.setRemainingPoint(bucket.getRemainingPoint() - usedPoint);
            bucket.setCreatedBy(getCurrentUsername());
            bucket.getCreatedAt();
            bucket.setUpdatedBy(getCurrentUsername());
            bucket.setUpdatedAt(now());
            trxLoyaltyPointBucketRepository.save(bucket);

            trxLoyaltyPointRedemptionDetailRepository.save(
                TrxLoyaltyPointRedemptionDetail.builder()
                        .ledger(newDataPointLedger)
                        .bucket(bucket)
                        .usedPoint(usedPoint)
                        .createdBy(getCurrentUsername())
                        .createdAt(now())
                    .build()
            );
            remainingRedeem -= usedPoint;
        }

        if (remainingRedeem > 0) throw new IllegalArgumentException("Bucket point tidak mencukupi");

        dataMember.setTotalPoint(afterBalance);
        customerMemberRepository.save(dataMember);

        dataSales.setCustomerMember(dataMember);
        dataSales.setMemberCodeSnapshot(dataMember.getMemberCode());
        dataSales.setMemberNameSnapshot(dataMember.getFullName());
        dataSales.setPointRedeemed(request.getRedeemPoint());
        dataSales.setPointDiscountAmount(redeemAmount);
        dataSales.setGrandTotal(grandTotal.subtract(redeemAmount));
        dataSales.setLoyaltyFormulaSnapshot(formulaResult.getContext());
        trxSalesRepository.save(dataSales);

        return RedeemPointResponse.builder()
                .salesNo(dataSales.getSalesNo())
                .memberCode(dataMember.getMemberCode())
                .redeemPoint(request.getRedeemPoint())
                .redeemAmount(redeemAmount)
                .remainingPoint(afterBalance)
            .build();
    }

    @Transactional
    @Override
    public void processEarnPoint(TrxSales dataSales) {
        if (!SalesStatus.PAID.equals(dataSales.getStatus())) return;
        if (dataSales == null || dataSales.getCustomerMember() == null) return;
        if (Boolean.TRUE.equals(dataSales.getLoyaltyProcessedFlag())) return;
        if (trxLoyaltyPointLedgerRepository.existsBySalesNoAndPointType(dataSales.getSalesNo(), LoyaltyPointType.EARN)) return;

        CustomerMember dataMember = customerMemberRepository.findByIdForUpdate(dataSales.getCustomerMember().getId()).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));

        validationDataMember(dataMember);

        LoyaltyRule dataLoyaltyRule = loyaltyRuleRepository.findActiveRule().orElseThrow(() -> new IllegalArgumentException("Rule loyalty aktif tidak ditemukan"));
        BigDecimal netAmount = dataSales.getGrandTotal();

        if (netAmount == null || netAmount.compareTo(BigDecimal.ZERO) <= 0) return;
        if (netAmount.compareTo(dataLoyaltyRule.getMinimumTransactionAmount()) < 0) return;

        Map<String, BigDecimal> variables = new HashMap<>();
        variables.put("NET_AMOUNT", netAmount);
        variables.put("AMOUNT_PER_POINT", dataLoyaltyRule.getAmountPerPoint());
        variables.put("POINT_EARNED", dataLoyaltyRule.getPointEarned());
        variables.put("TIER_MULTIPLIER", dataMember.getMemberTier().getPointMultiplier());

        FormulaCalculationResult formulaResult = formulaEngineService.evaluate("LOYALTY_EARN_POINT", variables);

        long earnedPoint = formulaResult.getResult().setScale(0, RoundingMode.DOWN).longValue();

        if (earnedPoint <= 0) return;

        long beforeBalance = dataMember.getTotalPoint();
        long afterBalance = beforeBalance + earnedPoint;

        String referenceNo = dataSales.getSalesNo();

        TrxLoyaltyPointLedger newDataPointLedger = trxLoyaltyPointLedgerRepository.save(
            TrxLoyaltyPointLedger.builder()
                    .customerMember(dataMember)
                    .salesNo(dataSales.getSalesNo())
                    .referenceNo(referenceNo)
                    .pointType(LoyaltyPointType.EARN)
                    .pointAmount(earnedPoint)
                    .balanceBefore(beforeBalance)
                    .balanceAfter(afterBalance)
                    .description("Point dari transaksi sales " + dataSales.getSalesNo())
                    .formulaId(formulaResult.getFormulaId())
                    .formulaVersionId(formulaResult.getFormulaVersionId())
                    .formulaExpressionSnapshot(formulaResult.getExpression())
                    .formulaContextSnapshot(formulaResult.getContext())
                    .formulaResultSnapshot(formulaResult.getResult())
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build()
        );

        trxLoyaltyPointBucketRepository.save(
            TrxLoyaltyPointBucket.builder()
                    .customerMember(dataMember)
                    .ledger(newDataPointLedger)
                    .totalPoint(earnedPoint)
                    .remainingPoint(earnedPoint)
                    .expiredAt(LocalDateTime.now().plusDays(dataLoyaltyRule.getExpiredAfterDays()))
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build()
        );

        dataMember.setTotalPoint(afterBalance);
        dataMember.setTotalSpending(dataMember.getTotalSpending().add(netAmount));
        customerMemberRepository.save(dataMember);

        dataSales.setPointEarned(earnedPoint);
        dataSales.setLoyaltyProcessedFlag(true);
        dataSales.setLoyaltyFormulaSnapshot(formulaResult.getContext());
        trxSalesRepository.save(dataSales);
    }

    @Transactional
    @Override
    public void reverseEarnPoint(String salesNo) {
        TrxLoyaltyPointLedger dateEarnLedger = trxLoyaltyPointLedgerRepository.findBySalesNoAndPointType(salesNo, LoyaltyPointType.EARN).orElse(null);

        if (dateEarnLedger == null) return;
        if (trxLoyaltyPointLedgerRepository.existsBySalesNoAndPointType(salesNo, LoyaltyPointType.REVERSE_EARN)) return;

        CustomerMember dataMember = customerMemberRepository.findByIdForUpdate(dateEarnLedger.getCustomerMember().getId()).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));
        long reversePoint = Math.abs(dateEarnLedger.getPointAmount());
        long beforeBalance = dataMember.getTotalPoint();
        long afterBalance = Math.max(0, beforeBalance - reversePoint);

        trxLoyaltyPointLedgerRepository.save(
            TrxLoyaltyPointLedger.builder()
                    .customerMember(dataMember)
                    .salesNo(salesNo)
                    .referenceNo("REV-EARN-" + salesNo)
                    .pointType(LoyaltyPointType.REVERSE_EARN)
                    .pointAmount(reversePoint * -1)
                    .balanceBefore(beforeBalance)
                    .balanceAfter(afterBalance)
                    .description("Reverse earn point sales " + salesNo)
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build()
        );

        dataMember.setTotalPoint(afterBalance);
        customerMemberRepository.save(dataMember);
    }

    @Transactional
    @Override
    public void reverseRedeemPoint(String salesNo) {
        TrxLoyaltyPointLedger dataPointLedger = trxLoyaltyPointLedgerRepository.findBySalesNoAndPointType(salesNo, LoyaltyPointType.REDEEM).orElse(null);
        if (dataPointLedger == null) return;
        if (trxLoyaltyPointLedgerRepository.existsBySalesNoAndPointType(salesNo, LoyaltyPointType.REVERSE_REDEEM)) return;

        CustomerMember dataMember = customerMemberRepository.findByIdForUpdate(dataPointLedger.getCustomerMember().getId()).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));
        long restoredPoint = Math.abs(dataPointLedger.getPointAmount());
        long beforeBalance = dataMember.getTotalPoint();
        long afterBalance = beforeBalance + restoredPoint;

        trxLoyaltyPointLedgerRepository.save(
            TrxLoyaltyPointLedger.builder()
                    .customerMember(dataMember)
                    .salesNo(salesNo)
                    .referenceNo("REV-REDEEM-" + salesNo)
                    .pointType(LoyaltyPointType.REVERSE_REDEEM)
                    .pointAmount(restoredPoint)
                    .balanceBefore(beforeBalance)
                    .balanceAfter(afterBalance)
                    .description("Restore redeem point sales " + salesNo)
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build()
        );

        dataMember.setTotalPoint(afterBalance);
        customerMemberRepository.save(dataMember);
    }

    @Transactional
    @Override
    public Long reverseEarnPointForReturn(String salesNo, String returnNo, BigDecimal returnAmount) {
        TrxLoyaltyPointLedger earnLedger = trxLoyaltyPointLedgerRepository.findBySalesNoAndPointType(salesNo, LoyaltyPointType.EARN).orElse(null);

        if (earnLedger == null) return 0L;

        if (trxLoyaltyPointLedgerRepository.existsByReferenceNoAndPointType(returnNo, LoyaltyPointType.RETURN_REVERSE_EARN)) return 0L;

        TrxSales sales = trxSalesRepository.findBySalesNoForUpdate(salesNo).orElseThrow(() -> new IllegalArgumentException("Transaksi sales tidak ditemukan"));

        if (sales.getGrandTotal() == null || sales.getGrandTotal().compareTo(BigDecimal.ZERO) <= 0) return 0L;

        CustomerMember member = customerMemberRepository.findByIdForUpdate(earnLedger.getCustomerMember().getId()).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));
        BigDecimal ratio = returnAmount.divide(sales.getGrandTotal(), 6, RoundingMode.DOWN);
        long reversePoint = BigDecimal.valueOf(Math.abs(earnLedger.getPointAmount())).multiply(ratio).setScale(0, RoundingMode.DOWN).longValue();

        if (reversePoint <= 0) return 0L;

        long beforeBalance = member.getTotalPoint();
        long afterBalance = Math.max(0, beforeBalance - reversePoint);

        trxLoyaltyPointLedgerRepository.save(
            TrxLoyaltyPointLedger.builder()
                    .customerMember(member)
                    .salesNo(salesNo)
                    .referenceNo(returnNo)
                    .pointType(LoyaltyPointType.RETURN_REVERSE_EARN)
                    .pointAmount(reversePoint * -1)
                    .balanceBefore(beforeBalance)
                    .balanceAfter(afterBalance)
                    .description("Reverse earn point karena return " + returnNo)
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build()
        );

        member.setTotalPoint(afterBalance);
        customerMemberRepository.save(member);

        return reversePoint;
    }

    @Transactional
    @Override
    public void restoreEarnPointForCancelledReturn(String salesNo, String returnNo) {
        TrxLoyaltyPointLedger returnReverseLedger = trxLoyaltyPointLedgerRepository.findByReferenceNoAndPointType(returnNo, LoyaltyPointType.RETURN_REVERSE_EARN).orElse(null);

        if (returnReverseLedger == null) return;
        if (trxLoyaltyPointLedgerRepository.existsByReferenceNoAndPointType(returnNo, LoyaltyPointType.RETURN_RESTORE_REDEEM)) return;

        CustomerMember member = customerMemberRepository.findByIdForUpdate(returnReverseLedger.getCustomerMember().getId()).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));
        long restorePoint = Math.abs(returnReverseLedger.getPointAmount());
        long beforeBalance = member.getTotalPoint();
        long afterBalance = beforeBalance + restorePoint;

        trxLoyaltyPointLedgerRepository.save(
            TrxLoyaltyPointLedger.builder()
                    .customerMember(member)
                    .salesNo(salesNo)
                    .referenceNo(returnNo)
                    .pointType(LoyaltyPointType.RETURN_RESTORE_REDEEM)
                    .pointAmount(restorePoint)
                    .balanceBefore(beforeBalance)
                    .balanceAfter(afterBalance)
                    .description("Restore point karena return dibatalkan " + returnNo)
                    .createdBy(getCurrentUsername())
                    .createdAt(now())
                .build()
        );

        member.setTotalPoint(afterBalance);
        customerMemberRepository.save(member);
    }

}
