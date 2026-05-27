package com.epos.backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mst_formula_allowed_function", schema = "public")
public class LstFormulaAllowedFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "function_code", nullable = false, unique = true)
    private String functionCode;

    @Column(name = "description")
    private String description;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;
    
}
