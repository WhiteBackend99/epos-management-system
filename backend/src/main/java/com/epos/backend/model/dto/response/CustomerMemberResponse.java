package com.epos.backend.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerMemberResponse {

    private Long id;
    private String memberCode;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String gender;
    private LocalDate birthDate;
    private String address;
    private String memberStatus;
    private String tierCode;
    private String tierName;
    private Long totalPoint;
    private BigDecimal totalSpending;
    
}
