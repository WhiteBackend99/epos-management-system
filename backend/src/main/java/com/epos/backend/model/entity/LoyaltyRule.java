package com.epos.backend.model.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper=true)
@Data
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mst_loyalty_rule", schema = "public")
public class LoyaltyRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rule_code", nullable = false, unique = true)
    private String ruleCode;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "earn_formula_id", nullable = false)
    private Formula earnFormula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redeem_formula_id", nullable = false)
    private Formula redeemFormula;

    @Column(name = "amount_per_point", nullable = false)
    private BigDecimal amountPerPoint;

    @Column(name = "point_earned", nullable = false)
    private BigDecimal pointEarned;

    @Column(name = "point_value_amount", nullable = false)
    private BigDecimal pointValueAmount;

    @Column(name = "max_redeem_percentage", nullable = false)
    private BigDecimal maxRedeemPercentage;

    @Column(name = "minimum_transaction_amount", nullable = false)
    private BigDecimal minimumTransactionAmount;

    @Column(name = "expired_after_days", nullable = false)
    private Long expiredAfterDays;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

}
