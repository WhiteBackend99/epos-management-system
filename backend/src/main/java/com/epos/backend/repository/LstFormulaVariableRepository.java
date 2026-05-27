package com.epos.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.LstFormulaVariable;

public interface LstFormulaVariableRepository extends JpaRepository<LstFormulaVariable, Long> {

    public  List<LstFormulaVariable> findByActiveFlagTrue();
    
}
