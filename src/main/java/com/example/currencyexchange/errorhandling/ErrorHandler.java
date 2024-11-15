package com.example.currencyexchange.errorhandling;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(MethodArgumentNotValidException ex) {
        logError(ex);
        var errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, errorMessages);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(HttpMessageNotReadableException ex) {
        logError(ex);
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(ResourceAccessException ex) {
        logError(ex);
        return response(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(ConnectException ex) {
        logError(ex);
        return response(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(SerializationException ex) {
        logError(ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(NBPIntegrationException ex) {
        logError(ex);
        return response(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getMessage());
    }
    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(IllegalArgumentException ex) {
        logError(ex);
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(AccountNotFoundException ex) {
        logError(ex);
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(AccountAlreadyExistsException ex) {
        logError(ex);
        return response(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(RedisException ex) {
        logError(ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handle(ConstraintViolationException ex) {
        logError(ex);
        var errorMessages = ex.getConstraintViolations().stream()
                .map(violation -> String.format("Field '%s' %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("; "));
        return response(HttpStatus.BAD_REQUEST, errorMessages);
    }

    private ResponseEntity<ErrorDto> response (HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorDto(message));
    }

    private static void logError(Exception ex) {
        log.error("Caught error : {}", ex.getMessage(), ex);
    }
}
