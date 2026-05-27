package com.epos.backend.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.epos.backend.enums.MemberStatus;
import com.epos.backend.model.dto.request.CreateCustomerMemberRequest;
import com.epos.backend.model.dto.request.UpdateCustomerMemberRequest;
import com.epos.backend.model.dto.request.search.CustomerMemberSearchRequest;
import com.epos.backend.model.dto.response.CustomerMemberResponse;
import com.epos.backend.model.entity.CustomerMember;
import com.epos.backend.model.entity.MemberTier;
import com.epos.backend.repository.CustomerMemberRepository;
import com.epos.backend.repository.MemberTierRepository;
import com.epos.backend.service.AutomationGeneratorServices;
import com.epos.backend.service.CustomerMemberService;
import com.epos.backend.specification.CustomerMemberSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerMemberServiceImpl extends Services implements CustomerMemberService {
    
    private final CustomerMemberRepository customerMemberRepository;
    private final MemberTierRepository memberTierRepository;
    private final AutomationGeneratorServices automationGeneratorServices;

    private CustomerMemberResponse buildEntityToResponse(CustomerMember data) {
        return CustomerMemberResponse.builder()
            .id(data.getId())
            .memberCode(data.getMemberCode())
            .fullName(data.getFullName())
            .phoneNumber(data.getPhoneNumber())
            .email(data.getEmail())
            .gender(data.getGender())
            .birthDate(data.getBirthDate())
            .address(data.getAddress())
            .memberStatus(data.getMemberStatus().name())
            .tierCode(data.getMemberTier().getTierCode())
            .tierName(data.getMemberTier().getTierName())
            .totalPoint(data.getTotalPoint())
            .totalSpending(data.getTotalSpending())
            .build();
    }
    
    private CustomerMemberResponse sectionUpdateStatus(Long id, MemberStatus status) {
        CustomerMember data = customerMemberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));
        data.setMemberStatus(status);
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());
        return buildEntityToResponse(customerMemberRepository.save(data));
    }

    private void validationDataRequest(CreateCustomerMemberRequest request) {
        if (customerMemberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Nomor HP sudah terdaftar");
        }

        if (StringUtils.hasText(request.getEmail()) &&
                customerMemberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }
    }

    @Transactional
    @Override
    public CustomerMemberResponse createData(CreateCustomerMemberRequest request) {
        validationDataRequest(request);

        MemberTier dataTier = memberTierRepository.findByTierCodeAndActiveFlagTrue("REGULAR").orElseThrow(() -> new IllegalArgumentException("Tier default REGULAR tidak ditemukan"));

        CustomerMember member = CustomerMember.builder()
            .memberCode(automationGeneratorServices.generateMemberCode())
            .fullName(request.getFullName())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .gender(request.getGender())
            .birthDate(request.getBirthDate())
            .address(request.getAddress())
            .memberStatus(MemberStatus.ACTIVE)
            .memberTier(dataTier)
            .totalPoint(0L)
            .totalSpending(BigDecimal.ZERO)
            .activeFlag(true)
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .build();

        return buildEntityToResponse(customerMemberRepository.save(member));
    }

    @Transactional
    @Override
    public CustomerMemberResponse updateData(Long id, UpdateCustomerMemberRequest request) {
        CustomerMember data = customerMemberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));

        data.setFullName(request.getFullName());
        data.setPhoneNumber(request.getPhoneNumber());
        data.setEmail(request.getEmail());
        data.setGender(request.getGender());
        data.setBirthDate(request.getBirthDate());
        data.setAddress(request.getAddress());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());

        return buildEntityToResponse(customerMemberRepository.save(data));
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerMemberResponse getDataById(Long id) {
        return customerMemberRepository.findById(id).map(this::buildEntityToResponse).orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerMemberResponse> searchData(CustomerMemberSearchRequest request) {
        int page = request.getPage() == null ? 0 : request.getPage();
        int size = request.getSize() == null ? 10 : request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return customerMemberRepository.findAll(CustomerMemberSpecification.filter(request), pageable).map(this::buildEntityToResponse);
    }

    @Transactional
    @Override
    public CustomerMemberResponse activate(Long id) {
        return sectionUpdateStatus(id, MemberStatus.ACTIVE);
    }

    @Transactional
    @Override
    public CustomerMemberResponse deactivate(Long id) {
        return sectionUpdateStatus(id, MemberStatus.INACTIVE);
    }

    @Transactional
    @Override
    public CustomerMemberResponse block(Long id) {
        return sectionUpdateStatus(id, MemberStatus.BLOCKED);
    }

    @Transactional
    @Override
    public CustomerMemberResponse suspend(Long id) {
        return sectionUpdateStatus(id, MemberStatus.SUSPENDED);
    }

}
