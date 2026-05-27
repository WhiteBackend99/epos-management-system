package com.epos.backend.service;

import java.math.BigDecimal;
import java.util.Map;

import com.epos.backend.model.dto.result.FormulaCalculationResult;


public interface FormulaEngineService {

    public FormulaCalculationResult evaluate(String formulaCode, Map<String, BigDecimal> variables);
    
}
