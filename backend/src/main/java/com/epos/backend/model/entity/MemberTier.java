package com.epos.backend.model.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "mst_member_tier", schema = "public")
public class MemberTier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tier_code", nullable = false, unique = true)
    private String tierCode;

    @Column(name = "tier_name", nullable = false)
    private String tierName;

    @Column(name = "minimum_spending", nullable = false)
    private BigDecimal minimumSpending;

    @Column(name = "point_multiplier", nullable = false)
    private BigDecimal pointMultiplier;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

}
