package com.example.just.Exception;

import co.elastic.clients.elasticsearch._types.ErrorResponse;
import com.google.api.gax.rpc.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // -> 전역 예외 처리 어노테이션
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.error("NotFoundException 발생: {}", ex.getMessage());
        return CustomErrorResponse.create(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<CustomErrorResponse> handleJwtValidException(Exception ex) {
        log.error("JWT 오류 발생: {}", ex.getMessage());
        return CustomErrorResponse.create(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneralException(Exception ex) {
        log.error("예상치 못한 오류 발생: {}", ex.getMessage());
        return CustomErrorResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
