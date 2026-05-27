package com.epos.backend.model.dto.request.search;

import com.epos.backend.enums.MemberStatus;

import lombok.Data;

@Data
public class CustomerMemberSearchRequest {

    private String keyword;
    private String memberCode;
    private String fullName;
    private String phoneNumber;
    private String email;
    private MemberStatus memberStatus;
    private Long memberTierId;
    private Integer page = 0;
    private Integer size = 10;

}
