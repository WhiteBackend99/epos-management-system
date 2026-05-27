package com.epos.backend.model.entity;

import java.time.LocalDateTime;

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
@Table(name = "mst_formula_version", schema = "public")
public class FormulaVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formula_id", nullable = false)
    private Formula formula;

    @Column(name = "version_no", nullable = false)
    private Long versionNo;

    @Column(name = "expression", nullable = false ,columnDefinition = "TEXT")
    private String expression;

    @Column(name = "effective_start", nullable = false)
    private LocalDateTime effectiveStart;

    @Column(name = "effective_end")
    private LocalDateTime effectiveEnd;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

    @Column(name = "rouding_mode")
    private String roundingMode;

    @Column(name = "scale_value")
    private Long scaleValue;

    @Column(name = "priority")
    private Long priority;

}
