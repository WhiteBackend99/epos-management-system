package com.epos.backend.model.entity;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.epos.backend.enums.PromoOperator;
import com.epos.backend.enums.PromoRuleType;

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
@Table(name = "mst_promo_rule", schema = "public")
public class PromoRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promo_id", nullable = false)
    private Promo promo;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type")
    private PromoRuleType ruleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator")
    private PromoOperator operator;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rule_value", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> ruleValue;
    
}
