package com.epos.backend.model.dto.response;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemParameterResponse {

    private Long id;
    private String parameterCode;
    private String parameterName;
    private String parameterValue;
    private String parameterType;
    private String description;
    private Boolean isActive;
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
    
}
