package com.eventledger.gateway.exception;

import com.eventledger.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFound(
            EventNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                404,
                                "NOT_FOUND",
                                ex.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(error -> error.getDefaultMessage())
                        .orElse("Validation failed");

        return ResponseEntity.badRequest()
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                400,
                                "VALIDATION_ERROR",
                                message,
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.internalServerError()
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                500,
                                "INTERNAL_SERVER_ERROR",
                                ex.getMessage(),
                                request.getRequestURI()
                        )
                );
    }
    @ExceptionHandler(
            AccountServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse>
    handleAccountServiceUnavailable(
            AccountServiceUnavailableException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                503,
                                "SERVICE_UNAVAILABLE",
                                ex.getMessage(),
                                request.getRequestURI()
                        )
                );
    }
}