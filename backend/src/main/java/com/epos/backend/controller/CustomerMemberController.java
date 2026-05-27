package com.epos.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epos.backend.annotation.AuditLog;
import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;
import com.epos.backend.model.dto.request.CreateCustomerMemberRequest;
import com.epos.backend.model.dto.request.UpdateCustomerMemberRequest;
import com.epos.backend.model.dto.request.search.CustomerMemberSearchRequest;
import com.epos.backend.model.dto.response.CustomerMemberResponse;
import com.epos.backend.model.dto.response.ResponseData;
import com.epos.backend.service.CustomerMemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer-members")
@RequiredArgsConstructor
public class CustomerMemberController {

    private final CustomerMemberService customerMemberService;

    @AuditLog(
        type = AuditType.CUSTOMER_MEMBER,
        action = AuditAction.VIEW
    )
    @PostMapping(value = "/search-data")
    public ResponseEntity<ResponseData<Page<CustomerMemberResponse>>> searchData(@Valid @RequestBody CustomerMemberSearchRequest request) {
        ResponseData<Page<CustomerMemberResponse>> response = ResponseData.<Page<CustomerMemberResponse>> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Data member berhasil diambil")
            .data(customerMemberService.searchData(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CUSTOMER_MEMBER,
        action = AuditAction.CREATE
    )
    @PostMapping(value = "/create-data")
    public ResponseEntity<ResponseData<CustomerMemberResponse>> createData(@Valid @RequestBody CreateCustomerMemberRequest request) {
        ResponseData<CustomerMemberResponse> response = ResponseData.<CustomerMemberResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Member berhasil dibuat")
            .data(customerMemberService.createData(request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CUSTOMER_MEMBER,
        action = AuditAction.UPDATE,
        referenceIdField = "id"
    )
    @PutMapping(value = "/update-data/{id}")
    public ResponseEntity<ResponseData<CustomerMemberResponse>> updateData(@PathVariable Long id, @Valid @RequestBody UpdateCustomerMemberRequest request) {
        ResponseData<CustomerMemberResponse> response = ResponseData.<CustomerMemberResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Member berhasil diperbarui")
            .data(customerMemberService.updateData(id, request))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CUSTOMER_MEMBER,
        action = AuditAction.VIEW,
        referenceIdField = "id"
    )
    @GetMapping(value = "/get-data/{id}")
    public ResponseEntity<ResponseData<CustomerMemberResponse>> getDataById(@PathVariable Long id) {
        ResponseData<CustomerMemberResponse> response = ResponseData.<CustomerMemberResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Data member berhasil diambil")
            .data(customerMemberService.getDataById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CUSTOMER_MEMBER,
        action = AuditAction.ACTIVE,
        referenceIdField = "id"
    )
    @PatchMapping(value = "/set-member-active/{id}")
    public ResponseEntity<ResponseData<CustomerMemberResponse>> setMemberActive(@PathVariable Long id) {
        ResponseData<CustomerMemberResponse> response = ResponseData.<CustomerMemberResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Member berhasil diaktifkan")
            .data(customerMemberService.activate(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CUSTOMER_MEMBER,
        action = AuditAction.DEACTIVE,
        referenceIdField = "id"
    )
    @PatchMapping(value = "/set-member-deactive/{id}")
    public ResponseEntity<ResponseData<CustomerMemberResponse>> setMemberDeactive(@PathVariable Long id) {
        ResponseData<CustomerMemberResponse> response = ResponseData.<CustomerMemberResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Member berhasil dinonaktifkan")
            .data(customerMemberService.deactivate(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CUSTOMER_MEMBER,
        action = AuditAction.SUSPEND,
        referenceIdField = "id"
    )
    @PatchMapping(value = "/set-member-suspend/{id}")
    public ResponseEntity<ResponseData<CustomerMemberResponse>> setMemberSuspend(@PathVariable Long id) {
        ResponseData<CustomerMemberResponse> response = ResponseData.<CustomerMemberResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Member berhasil ditangguhkan")
            .data(customerMemberService.suspend(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @AuditLog(
        type = AuditType.CUSTOMER_MEMBER,
        action = AuditAction.BLOCKED,
        referenceIdField = "id"
    )
    @PatchMapping(value = "/set-member-block/{id}")
    public ResponseEntity<ResponseData<CustomerMemberResponse>> setMemberBlock(@PathVariable Long id) {
        ResponseData<CustomerMemberResponse> response = ResponseData.<CustomerMemberResponse> builder()
            .success(true)
            .code(HttpStatus.OK.value())
            .message("Member berhasil diblokir")
            .data(customerMemberService.block(id))
            .build();
        return ResponseEntity.ok(response);
    }

}
