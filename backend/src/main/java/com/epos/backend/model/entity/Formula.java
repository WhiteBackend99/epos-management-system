package com.epos.backend.model.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "mst_formula", schema = "public")
public class Formula extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "formula_code", nullable = false, unique = true)
    private String formulaCode;

    @Column(name = "formula_name" ,nullable = false)
    private String formulaName;

    @Column(name = "module_code", nullable = false)
    private String moduleCode;

    @Column(name = "formula_type", nullable = false)
    private String formulaType;

    @Column(name = "description")
    private String description;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

    @OneToMany(mappedBy = "formula", fetch = FetchType.LAZY)
    private List<FormulaVersion> versions;

}
