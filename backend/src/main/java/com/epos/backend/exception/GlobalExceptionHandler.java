package com.epos.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.epos.backend.model.dto.response.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Object>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream().findFirst().map(err -> err.getField() + " " + err.getDefaultMessage()).orElse("Validasi Gagal");

        ResponseData<Object> response = ResponseData.builder().success(false).message(e.getMessage()).code(HttpStatus.BAD_REQUEST.value()).data(message).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(exception = RuntimeException.class)
    public ResponseEntity<ResponseData<Object>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime Exception occurred", e);

        ResponseData<Object> response = ResponseData.builder().success(false).message(e.getMessage()).code(HttpStatus.BAD_REQUEST.value()).data(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<ResponseData<Object>> handleGeneralException(Exception e) {
        log.error("Unhandled exception occurred", e);

        ResponseData<Object> response = ResponseData.builder().success(false).message(e.getMessage()).code(HttpStatus.INTERNAL_SERVER_ERROR.value()).data("Internal Server Error").build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
