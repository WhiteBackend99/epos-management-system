package com.epos.backend.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.SystemParameterRequest;
import com.epos.backend.model.dto.request.search.SystemParameterSearchRequest;
import com.epos.backend.model.dto.response.SystemParameterResponse;

public interface SystemParameterService {

    public SystemParameterResponse createData(SystemParameterRequest request);
    public SystemParameterResponse updateData(Long id, SystemParameterRequest request);
    public SystemParameterResponse getDataById(Long id);
    public SystemParameterResponse getDataByCode(String parameterCode);
    public SystemParameterResponse deleteData(Long id);
    public Page<SystemParameterResponse> searchData(SystemParameterSearchRequest request);
    public String getStringValue(String code, String defaultValue);
    public BigDecimal getDecimalValue(String code, BigDecimal defaultValue);
    public Integer getIntegerValue(String code, Integer defaultValue);
    public Boolean getBooleanValue(String code, Boolean defaultValue);

}
