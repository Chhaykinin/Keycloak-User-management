package com.company.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* -------------------------------------------------
       VALIDATION ERROR (DTO @Valid)
    ------------------------------------------------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<FieldError>>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        BindingResult bindingResult = ex.getBindingResult();

        List<FieldError> fieldErrors = bindingResult
                .getFieldErrors()
                .stream()
                .map(err -> FieldError.builder()
                        .field(err.getField())
                        .detail(err.getDefaultMessage())
                        .build()
                )
                .toList();

        ErrorResponse<List<FieldError>> response =
                ErrorResponse.<List<FieldError>>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(fieldErrors)
                        .build();

        return ResponseEntity.badRequest().body(response);
    }

    /* -------------------------------------------------
       NOT FOUND
    ------------------------------------------------- */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse<String>> handleNotFound(
            ResourceNotFoundException ex
    ) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /* -------------------------------------------------
       CONFLICT (optional but recommended)
    ------------------------------------------------- */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse<String>> handleConflict(
            ConflictException ex
    ) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    /* -------------------------------------------------
       GENERIC ERROR
    ------------------------------------------------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<String>> handleGeneric(
            Exception ex
    ) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error"
        );
    }

    /* -------------------------------------------------
       COMMON BUILDER
    ------------------------------------------------- */
    private ResponseEntity<ErrorResponse<String>> buildError(
            HttpStatus status,
            String message
    ) {
        ErrorResponse<String> response =
                ErrorResponse.<String>builder()
                        .statusCode(status.value())
                        .message(message)
                        .build();

        return ResponseEntity.status(status).body(response);
    }


}
