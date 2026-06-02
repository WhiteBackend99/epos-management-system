package com.epos.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.epos.backend.exception.ConflictException;
import com.epos.backend.exception.NotFoundException;
import com.epos.backend.model.dto.request.SupplierRequest;
import com.epos.backend.model.dto.request.search.SupplierSearchRequest;
import com.epos.backend.model.dto.response.SupplierResponse;
import com.epos.backend.model.entity.Supplier;
import com.epos.backend.repository.SupplierRepository;
import com.epos.backend.service.SupplierService;
import com.epos.backend.specification.SupplierSpecification;
import com.epos.backend.util.ParseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends Services implements SupplierService {
    
    private final SupplierRepository supplierRepository;

    private void validationCreate(SupplierRequest request) {
        if (supplierRepository.existsByNameIgnoreCaseAndIsDeletedFalse(request.getName().trim())) {
            throw new ConflictException("Supplier dengan nama tersebut sudah ada");
        }

        if (StringUtils.hasText(request.getPhone()) && supplierRepository.existsByPhoneAndIsDeletedFalse(request.getPhone().trim())) {
            throw new ConflictException("Nomor telepon supplier sudah digunakan");
        }

        if (StringUtils.hasText(request.getEmail()) && supplierRepository.existsByEmailIgnoreCaseAndIsDeletedFalse(request.getEmail().trim())) {
            throw new ConflictException("Email supplier sudah digunakan");
        }
    }

    private void validationUpdate(Long id, SupplierRequest request) {
        if (supplierRepository.existsByNameIgnoreCaseAndIdNotAndIsDeletedFalse(request.getName().trim(), id)) {
            throw new ConflictException("Supplier dengan nama tersebut sudah ada");
        }

        if (StringUtils.hasText(request.getPhone()) && supplierRepository.existsByPhoneAndIdNotAndIsDeletedFalse(request.getPhone().trim(), id)) {
            throw new ConflictException("Nomor telepon supplier sudah digunakan");
        }

        if (StringUtils.hasText(request.getEmail()) && supplierRepository.existsByEmailIgnoreCaseAndIdNotAndIsDeletedFalse(request.getEmail().trim(), id)) {
            throw new ConflictException("Email supplier sudah digunakan");
        }
    }

    private SupplierResponse buildEntityToResponse(Supplier data) {
        return SupplierResponse.builder()
                .id(data.getId())
                .supplierCode(data.getSupplierCode())
                .name(data.getName())
                .phone(data.getPhone())
                .email(data.getEmail())
                .address(data.getAddress())
                .picName(data.getPicName())
                .notes(data.getNotes())
                .isActive(data.getIsActive())
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())
                .updatedBy(data.getUpdatedBy())
                .updatedAt(data.getUpdatedAt())
            .build();
    }
    
    @Transactional
    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        validationCreate(request);

        Supplier newData = Supplier.builder()
                .supplierCode(automationGeneratorServices.generateSupplierCode())
                .name(request.getName().trim())
                .phone(ParseUtil.trimToNull(request.getPhone()))
                .email(ParseUtil.toLowerTrim(request.getEmail()))
                .address(ParseUtil.trimToNull(request.getAddress()))
                .picName(ParseUtil.trimToNull(request.getPicName()))
                .notes(ParseUtil.trimToNull(request.getNotes()))
                .isActive(request.getIsActive() == null ? true : request.getIsActive())
                .isDeleted(false)
                .createdBy(getCurrentUsername())
                .createdAt(now())
            .build();

        return buildEntityToResponse(supplierRepository.save(newData));
    }

    @Transactional
    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier data = supplierRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Supplier tidak ditemukan"));

        validationUpdate(id, request);

        data.setName(request.getName().trim());
        data.setPhone(ParseUtil.trimToNull(request.getPhone()));
        data.setEmail(ParseUtil.toLowerTrim(request.getEmail()));
        data.setAddress(ParseUtil.trimToNull(request.getAddress()));
        data.setPicName(ParseUtil.trimToNull(request.getPicName()));
        data.setNotes(ParseUtil.trimToNull(request.getNotes()));
        data.setIsActive(request.getIsActive() == null ? data.getIsActive() : request.getIsActive());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());

        return buildEntityToResponse(supplierRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public SupplierResponse getSupplierById(Long id) {
        Supplier data = supplierRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NotFoundException("Supplier tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Transactional(readOnly = true)
    @Override
    public SupplierResponse deleteSupplier(Long id) {
        Supplier data = supplierRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Supplier tidak ditemukan"));
        data.setIsDeleted(true);
        data.setIsActive(false);
        data.setDeletedBy(getCurrentUsername());
        data.setDeletedAt(now());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntityToResponse(supplierRepository.save(data));
    }

    @Transactional
    @Override
    public SupplierResponse activateSupplier(Long id) {
        Supplier data = supplierRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Supplier tidak ditemukan"));
        data.setIsActive(true);
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntityToResponse(supplierRepository.save(data));
    }

    @Transactional
    @Override
    public SupplierResponse deactivateSupplier(Long id) {
        Supplier data = supplierRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Supplier tidak ditemukan"));
        data.setIsActive(false);
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntityToResponse(supplierRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SupplierResponse> searchData(SupplierSearchRequest request) {
        SupplierSearchRequest searchRequest = request == null ? new SupplierSearchRequest() : request;
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        return supplierRepository.findAll(SupplierSpecification.filter(searchRequest), pageable).map(this::buildEntityToResponse);
    }

}
