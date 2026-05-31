package com.epos.backend.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.exception.BusinessException;
import com.epos.backend.exception.ConflictException;
import com.epos.backend.exception.NotFoundException;
import com.epos.backend.model.dto.request.SystemParameterRequest;
import com.epos.backend.model.dto.request.search.SystemParameterSearchRequest;
import com.epos.backend.model.dto.response.SystemParameterResponse;
import com.epos.backend.model.entity.SystemParameter;
import com.epos.backend.repository.SystemParameterRepository;
import com.epos.backend.service.SystemParameterService;
import com.epos.backend.specification.SystemParameterSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemParameterServiceImpl extends Services implements SystemParameterService {
    
    private final SystemParameterRepository systemParameterRepository;

    private SystemParameterResponse buildEntityToResponse(SystemParameter data) {
        return SystemParameterResponse.builder()
                .id(data.getId())
                .parameterCode(data.getParameterCode())
                .parameterName(data.getParameterName())
                .parameterValue(data.getParameterValue())
                .parameterType(data.getParameterType())
                .description(data.getDescription())
                .isActive(data.getIsActive())
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())
                .updatedBy(data.getUpdatedBy())
                .updatedAt(data.getUpdatedAt())
            .build();
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) throw new BusinessException("Kode parameter wajib diisi");
        return code.trim().toUpperCase();
    }

    @Transactional
    @Override
    public SystemParameterResponse createData(SystemParameterRequest request) {
        String code = normalizeCode(request.getParameterCode());

        if (systemParameterRepository.existsByParameterCodeAndIsDeletedFalse(code)) throw new ConflictException("Kode parameter sudah digunakan");

        SystemParameter data = SystemParameter.builder()
                .parameterCode(code)
                .parameterName(request.getParameterName())
                .parameterValue(request.getParameterValue())
                .parameterType(request.getParameterType().toUpperCase())
                .description(request.getDescription())
                .isActive(request.getIsActive() == null ? true : request.getIsActive())
                .isDeleted(false)
                .createdBy(getCurrentUsername())
                .createdAt(now())
            .build();

        return buildEntityToResponse(systemParameterRepository.save(data));
    }

    @Transactional
    @Override
    public SystemParameterResponse updateData(Long id, SystemParameterRequest request) {
        SystemParameter data = systemParameterRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Parameter tidak ditemukan"));

        String code = normalizeCode(request.getParameterCode());

        if (systemParameterRepository.existsByParameterCodeAndIdNotAndIsDeletedFalse(code, id)) throw new ConflictException("Kode parameter sudah digunakan");

        data.setParameterCode(code);
        data.setParameterName(request.getParameterName());
        data.setParameterValue(request.getParameterValue());
        data.setParameterType(request.getParameterType().toUpperCase());
        data.setDescription(request.getDescription());
        data.setIsActive(request.getIsActive() == null ? true : request.getIsActive());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());

        return buildEntityToResponse(systemParameterRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public SystemParameterResponse getDataById(Long id) {
        return systemParameterRepository.findById(id).filter(data -> Boolean.FALSE.equals(data.getIsDeleted())).map(this::buildEntityToResponse).orElseThrow(() -> new NotFoundException("Parameter tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    @Override
    public SystemParameterResponse getDataByCode(String parameterCode) {
        return systemParameterRepository.findByParameterCodeAndIsDeletedFalse(normalizeCode(parameterCode)).map(this::buildEntityToResponse).orElseThrow(() -> new NotFoundException("Parameter tidak ditemukan"));
    }

    @Transactional
    @Override
    public SystemParameterResponse deleteData(Long id) {
        SystemParameter data = systemParameterRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Parameter tidak ditemukan"));

        data.setIsDeleted(true);
        data.setIsActive(false);
        data.setDeletedBy(getCurrentUsername());
        data.setDeletedAt(now());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());

        return buildEntityToResponse(systemParameterRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SystemParameterResponse> searchData(SystemParameterSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return systemParameterRepository.findAll(SystemParameterSpecification.search(request), pageable).map(this::buildEntityToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public String getStringValue(String code, String defaultValue) {
        return systemParameterRepository.findByParameterCodeAndIsDeletedFalse(normalizeCode(code)).filter(data -> Boolean.TRUE.equals(data.getIsActive())).map(SystemParameter::getParameterValue).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal getDecimalValue(String code, BigDecimal defaultValue) {
        String value = getStringValue(code, null);
        if (value == null) return defaultValue;
        
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            throw new BusinessException("Parameter " + code + " bukan angka decimal yang valid");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getIntegerValue(String code, Integer defaultValue) {
        String value = getStringValue(code, null);
        if (value == null) return defaultValue;

        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            throw new BusinessException("Parameter " + code + " bukan angka integer yang valid");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean getBooleanValue(String code, Boolean defaultValue) {
        String value = getStringValue(code, null);
        if (value == null) return defaultValue;
        return Boolean.valueOf(value);
    }

}
