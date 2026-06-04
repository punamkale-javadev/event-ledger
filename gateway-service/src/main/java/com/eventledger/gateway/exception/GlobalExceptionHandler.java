package com.eventledger.gateway.exception;

import com.eventledger.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse>
    handleEventNotFound(
            EventNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                404,
                                "NOT_FOUND",
                                ex.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
    handleValidation(
            MethodArgumentNotValidException ex) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .getFirst()
                        .getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                400,
                                "BAD_REQUEST",
                                message,
                                null
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleGeneric(Exception ex) {

        return ResponseEntity.internalServerError()
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                500,
                                "INTERNAL_SERVER_ERROR",
                                ex.getMessage(),
                                null
                        )
                );
    }
}