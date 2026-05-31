package com.epos.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.epos.backend.model.dto.response.ResponseData;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Object>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream().findFirst().map(FieldError::getDefaultMessage).orElse("Request tidak valid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseData.failed(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler({BadRequestException.class, BusinessException.class, IllegalArgumentException.class})
    public ResponseEntity<ResponseData<Object>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseData.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler({NotFoundException.class, EntityNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ResponseData<Object>> handleNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseData.failed(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseData<Object>> handleConflict(ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.failed(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseData<Object>> handleForbidden(AuthorizationDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseData.failed(HttpStatus.FORBIDDEN.value(), "Anda tidak memiliki akses untuk melakukan aksi ini"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseData<Object>> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("Database constraint error", e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.failed(HttpStatus.CONFLICT.value(), "Data tidak valid atau sudah digunakan"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Object>> handleGeneralException(Exception e) {
        log.error("Unhandled exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseData.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Terjadi kesalahan pada sistem"));
    }

}
