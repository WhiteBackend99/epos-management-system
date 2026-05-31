package com.epos.backend.model.dto.response;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseData<T> {

    private Boolean success;
    private Integer code;
    private String message;
    private T data;

    public static <T> ResponseData<T> success(String message, T data) {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
            .build();
    }

    public static <T> ResponseData<T> created(String message, T data) {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
            .build();
    }

    public static <T> ResponseData<T> failed(Integer code, String message, T data) {
        return ResponseData.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(data)
            .build();
    }

    public static <T> ResponseData<T> failed(Integer code, String message) {
        return ResponseData.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
            .build();
    }

}
