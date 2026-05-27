package com.epos.backend.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.epos.backend.model.dto.result.FormulaCalculationResult;
import com.epos.backend.model.entity.Formula;
import com.epos.backend.model.entity.FormulaVersion;
import com.epos.backend.model.entity.LstFormulaAllowedFunction;
import com.epos.backend.model.entity.LstFormulaVariable;
import com.epos.backend.repository.FormulaRepository;
import com.epos.backend.repository.FormulaVersionRepository;
import com.epos.backend.repository.LstFormulaAllowedFunctionRepository;
import com.epos.backend.repository.LstFormulaVariableRepository;
import com.epos.backend.service.FormulaEngineService;

import lombok.RequiredArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormulaEngineServiceImpl implements FormulaEngineService {
    
    private final FormulaRepository formulaRepository;
    private final FormulaVersionRepository formulaVersionRepository;
    private final LstFormulaVariableRepository lstFormulaVariableRepository;
    private final LstFormulaAllowedFunctionRepository lstFormulaAllowedFunctionRepository;

    private void validationExpression(String expression, Map<String, BigDecimal> variables) {
        if (!StringUtils.hasText(expression)) {
            throw new IllegalArgumentException("Expression formula tidak boleh kosong");
        }

        String lowerExpression = expression.toLowerCase();

        if (lowerExpression.contains(";") || lowerExpression.contains("runtime") || lowerExpression.contains("class") || 
            lowerExpression.contains("system") || lowerExpression.contains("exec") || lowerExpression.contains("script") ||
            lowerExpression.contains("java") ) {
            throw new IllegalArgumentException("Expression formula tidak valid");
        }

        Set<String> allowedVariables = lstFormulaVariableRepository.findByActiveFlagTrue().stream().map(LstFormulaVariable::getVariableCode).collect(Collectors.toSet());
        Set<String> allowedFunctions = lstFormulaAllowedFunctionRepository.findByActiveFlagTrue().stream().map(LstFormulaAllowedFunction::getFunctionCode).collect(Collectors.toSet());

        for (String variable : variables.keySet()) {
            if (!allowedVariables.contains(variable)) {
                throw new IllegalArgumentException("Variable formula tidak terdaftar: " + variable);
            }
        }

        Pattern tokenPattern = Pattern.compile("\\b[A-Z_][A-Z0-9_]*\\b");
        Matcher matcher = tokenPattern.matcher(expression);

        while (matcher.find()) {
            String token = matcher.group();
            boolean isVariable = variables.containsKey(token);
            boolean isFunction = allowedFunctions.contains(token);

            if (!isVariable && !isFunction) {
                throw new IllegalArgumentException("Token formula tidak valid: " + token);
            }
        }
    }

    private List<Function> getAllowedFunctions() {
        return List.of(
            new Function("FLOOR", 1) {
                @Override
                public double apply(double... args) {
                    return Math.floor(args[0]);
                }
            },
            new Function("CEIL", 1) {
                @Override
                public double apply(double... args) {
                    return Math.ceil(args[0]);
                }
            },
            new Function("ROUND", 1) {
                @Override
                public double apply(double... args) {
                    return Math.round(args[0]);
                }
            },
            new Function("ABS", 1) {
                @Override
                public double apply(double... args) {
                    return Math.abs(args[0]);
                }
            },
            new Function("MIN", 2) {
                @Override
                public double apply(double... args) {
                    return Math.min(args[0], args[1]);
                }
            },
            new Function("MAX", 2) {
                @Override
                public double apply(double... args) {
                    return Math.max(args[0], args[1]);
                }
            }
        );
    }

    @Override
    public FormulaCalculationResult evaluate(String formulaCode, Map<String, BigDecimal> variables) {
        Formula dataFormula = formulaRepository.findByFormulaCodeAndActiveFlagTrue(formulaCode).orElseThrow(() -> new IllegalArgumentException("Formula tidak ditemukan"));
        FormulaVersion dataFormulaVersion = formulaVersionRepository.findActiveVersion(formulaCode).orElseThrow(() -> new IllegalArgumentException("Versi formula aktif tidak ditemukan"));

        validationExpression(dataFormulaVersion.getExpression(), variables);

        Set<String> variableNames = variables.keySet();

        Expression expression = new ExpressionBuilder(dataFormulaVersion.getExpression())
            .variables(variableNames)
            .functions(getAllowedFunctions())
            .build();

        variables.forEach((key, value) -> expression.setVariable(key, value.doubleValue()));
        BigDecimal result = BigDecimal.valueOf(expression.evaluate());

        Map<String, Object> context = new HashMap<>();
        variables.forEach(context::put);

        return FormulaCalculationResult.builder()
            .formulaId(dataFormula.getId())
            .formulaVersionId(dataFormulaVersion.getId())
            .formulaCode(formulaCode)
            .expression(dataFormulaVersion.getExpression())
            .context(context)
            .result(result)
            .build();
    }

}
