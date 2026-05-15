package com.epos.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.model.dto.request.SupplierRequest;
import com.epos.backend.model.dto.request.search.SupplierSearchRequest;
import com.epos.backend.model.dto.response.SupplierResponse;
import com.epos.backend.model.entity.Supplier;
import com.epos.backend.repository.SupplierRepository;
import com.epos.backend.service.SupplierService;
import com.epos.backend.specification.SupplierSpecification;
import com.epos.backend.util.GeneratorUtil;
import com.epos.backend.util.ParseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends Services implements SupplierService {
    
    private final SupplierRepository supplierRepository;

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
            .createdBy(data.getCreatedBy())
            .createdAt(data.getCreatedAt())
            .updatedBy(data.getUpdatedBy())
            .updatedAt(data.getUpdatedAt())
            .build();
    }
    
    private void validationCreate(SupplierRequest request) {
        if (supplierRepository.existsByNameIgnoreCaseAndIsDeletedFalse(request.getName().trim())) {
            throw new RuntimeException("Supplier dengan nama tersebut sudah ada");
        }
        if (ParseUtil.hasText(request.getPhone()) && supplierRepository.existsByPhoneAndIsDeletedFalse(request.getPhone().trim())) {
            throw new RuntimeException("Nomor Telepon supplier sudah digunakan");
        }
        if (ParseUtil.hasText(request.getEmail()) && supplierRepository.existsByEmailIgnoreCaseAndIsDeletedFalse(request.getEmail().trim())) {
            throw new RuntimeException("Email supplier sudah digunakan");
        }
    }

    private void validationUpdate(Long id, SupplierRequest request) {
        if (supplierRepository.existsByNameIgnoreCaseAndIdNotAndIsDeletedFalse(request.getName().trim(), id)) {
            throw new RuntimeException("Supplier dengan nama tersebut sudah ada");
        }
        if (ParseUtil.hasText(request.getPhone()) && supplierRepository.existsByPhoneAndIdNotAndIsDeletedFalse(request.getPhone().trim(), id)) {
            throw new RuntimeException("Nomor Telepon supplier sudah digunakan");
        }
        if (ParseUtil.hasText(request.getEmail()) && supplierRepository.existsByEmailIgnoreCaseAndIdNotAndIsDeletedFalse(request.getEmail().trim(), id)) {
            throw new RuntimeException("Email supplier sudah digunakan");
        }
    }

    @Transactional
    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        validationCreate(request);

        Long sequence = supplierRepository.getNextSupplierCodeSequence();
        String supplierCode = GeneratorUtil.generateSupplierCode(sequence);

        Supplier newData = Supplier.builder()
            .supplierCode(supplierCode)
            .name(request.getName())
            .phone(ParseUtil.trimToNull(request.getPhone()))
            .email(ParseUtil.toLowerTrim(request.getEmail()))
            .address(ParseUtil.trimToNull(request.getAddress()))
            .picName(ParseUtil.trimToNull(request.getPicName()))
            .notes(ParseUtil.trimToNull(request.getNotes()))
            .isActive(request.getIsActive() != null ? request.getIsActive() : true)
            .isDeleted(false)
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .build();
        supplierRepository.save(newData);
        return buildEntityToResponse(newData);
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier data = supplierRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Supplier tidak ditemukan"));

        validationUpdate(id, request);

        data.setName(request.getName());
        data.setPhone(request.getPhone());
        data.setEmail(request.getEmail());
        data.setAddress(request.getAddress());
        data.setPicName(request.getPicName());
        data.setNotes(request.getNotes());
        data.setIsActive(request.getIsActive() != null ? request.getIsActive() : data.getIsActive());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        supplierRepository.save(data);
        return buildEntityToResponse(data);
    }

    @Override
    public SupplierResponse getSupplierById(Long id) {
        Supplier data = supplierRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Supplier tidak ditemukan"));
        return buildEntityToResponse(data);
    }

    @Override
    public Page<SupplierResponse> searchData(int page, int size, SupplierSearchRequest request) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Supplier> specification = SupplierSpecification.filter(request);
        return supplierRepository.findAll(specification, pageable).map(this::buildEntityToResponse);
    }

    @Transactional
    @Override
    public void deleteSupplier(Long id) {
        Supplier data = supplierRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Supplier tidak ditemukan"));
        data.setIsDeleted(true);
        data.setIsActive(false);
        data.setDeletedBy(getCurrentUsername());
        data.setDeletedAt(now());
        supplierRepository.save(data);
    }
    
}
