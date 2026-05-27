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
@Table(name = "lst_formula_variable", schema = "public")
public class LstFormulaVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "variable_code", nullable = false, unique = true)
    private String variableCode;

    @Column(name = "variable_name", nullable = false)
    private String variableName;

    @Column(name = "data_type", nullable = false)
    private String dataType;

    @Column(name = "description")
    private String description;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;
    
}
