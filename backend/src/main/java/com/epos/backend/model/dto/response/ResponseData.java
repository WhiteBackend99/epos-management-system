package com.epos.backend.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseData<T> {

    private Boolean success;
    private Integer code;
    private String message;
    private T data;

}
