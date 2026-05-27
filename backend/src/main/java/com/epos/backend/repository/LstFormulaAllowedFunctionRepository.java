package com.epos.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.LstFormulaAllowedFunction;

public interface LstFormulaAllowedFunctionRepository extends JpaRepository<LstFormulaAllowedFunction, Long> {

    public List<LstFormulaAllowedFunction> findByActiveFlagTrue();
    
}
