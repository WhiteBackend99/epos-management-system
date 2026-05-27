package com.epos.backend.model.dto.result;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FormulaCalculationResult {

    private Long formulaId;
    private Long formulaVersionId;
    private String formulaCode;
    private String expression;
    private Map<String, Object> context;
    private BigDecimal result;
    
}
