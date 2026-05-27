package com.epos.backend.service;

import org.springframework.data.domain.Page;

import com.epos.backend.model.dto.request.CreateCustomerMemberRequest;
import com.epos.backend.model.dto.request.UpdateCustomerMemberRequest;
import com.epos.backend.model.dto.request.search.CustomerMemberSearchRequest;
import com.epos.backend.model.dto.response.CustomerMemberResponse;

public interface CustomerMemberService {

    public CustomerMemberResponse createData(CreateCustomerMemberRequest request);
    public CustomerMemberResponse updateData(Long id, UpdateCustomerMemberRequest request);
    public CustomerMemberResponse getDataById(Long id);
    public Page<CustomerMemberResponse> searchData(CustomerMemberSearchRequest request);
    public CustomerMemberResponse activate(Long id);
    public CustomerMemberResponse deactivate(Long id);
    public CustomerMemberResponse block(Long id);
    public CustomerMemberResponse suspend(Long id);

}
