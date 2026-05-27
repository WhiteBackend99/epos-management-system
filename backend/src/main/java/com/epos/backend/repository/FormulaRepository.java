package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.Formula;

public interface FormulaRepository extends JpaRepository<Formula, Long> {

    public Optional<Formula> findByFormulaCodeAndActiveFlagTrue(String formulaCode);
    
}
