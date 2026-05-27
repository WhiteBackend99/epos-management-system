package com.epos.backend.model.entity;

import java.math.BigDecimal;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.epos.backend.enums.LoyaltyPointType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "trx_loyalty_point_ledger", schema = "public")
public class TrxLoyaltyPointLedger extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_member_id", nullable = false)
    private CustomerMember customerMember;

    @Column(name = "sales_no")
    private String salesNo;

    @Column(name = "reference_no", nullable = false)
    private String referenceNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false)
    private LoyaltyPointType pointType;

    @Column(name = "point_amount", nullable = false)
    private Long pointAmount;

    @Column(name = "balance_before", nullable = false)
    private Long balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    @Column(name = "description")
    private String description;

    @Column(name = "formula_id")
    private Long formulaId;

    @Column(name = "formula_version_id")
    private Long formulaVersionId;

    @Column(name = "formula_expression_snapshot", columnDefinition = "TEXT")
    private String formulaExpressionSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "formula_context_snapshot", columnDefinition = "jsonb")
    private Map<String, Object> formulaContextSnapshot;

    @Column(name = "formula_result_snapshot")
    private BigDecimal formulaResultSnapshot;

}
