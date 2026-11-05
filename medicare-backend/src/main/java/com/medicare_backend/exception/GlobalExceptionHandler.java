package com.medicare_backend.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handles patient not found and duplicate emails
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {

        return buildError(
                ex.getStatusCode(),
                ex.getReason() == null
                        ? "Request could not be completed"
                        : ex.getReason(),
                request,
                Map.of()
        );
    }

    // Handles invalid patient form information
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(field -> {
            errors.putIfAbsent(
                    field.getField(),
                    field.getDefaultMessage() == null
                            ? "Invalid value"
                            : field.getDefaultMessage()
            );
        });

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                errors
        );
    }

    // Handles incorrect JSON, such as age: "hello"
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid JSON or incorrect field type",
                request,
                Map.of()
        );
    }

    // Handles database constraint problems
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDatabaseConflict(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn("Database constraint violation", ex);

        return buildError(
                HttpStatus.CONFLICT,
                "Data conflicts with a database constraint",
                request,
                Map.of()
        );
    }

    // Handles unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error", ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred",
                request,
                Map.of()
        );
    }

    // Common method to create the error response
    private ResponseEntity<ApiError> buildError(
            HttpStatusCode status,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors) {

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                HttpStatus.valueOf(status.value()).getReasonPhrase(),
                message,
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(status).body(error);
    }
}
