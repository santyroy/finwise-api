package com.roy.finwise.exceptions;

import com.roy.finwise.dto.ApiErrorResponse;
import com.roy.finwise.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("BadRequest: {}", ex.getMessage());
        List<String> validationErrors = ex.getBindingResult().getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", request, validationErrors);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.error("NotFoundException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleUserAlreadyExistException(UserAlreadyExistException ex, WebRequest request) {
        log.error("UserAlreadyExistException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleCustomAuthenticationException(CustomAuthenticationException ex, WebRequest request) {
        log.error("CustomAuthenticationException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleDisabledUserException(DisabledException ex, WebRequest request) {
        log.error("User is disabled, need to verify OTP: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                "Please verify your account via OTP", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", request, null);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleJwtException(JwtException ex, WebRequest request) {
        log.error("JwtException: {}", ex.getMessage(), ex);
        String errorMessage;
        if (ex instanceof ExpiredJwtException) {
            errorMessage = "JWT token has expired";
        } else if (ex instanceof SignatureException) {
            errorMessage = "JWT signature is invalid";
        } else if (ex instanceof MalformedJwtException) {
            errorMessage = "JWT token is malformed";
        } else if (ex instanceof UnsupportedJwtException) {
            errorMessage = "JWT token type is unsupported";
        } else {
            errorMessage = "JWT token is invalid";
        }
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, errorMessage, request, null);
    }

    @ExceptionHandler(InvalidPeriodException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleInvalidPeriodException(Exception ex, WebRequest request) {
        log.error("InvalidPeriodException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    private ResponseEntity<ApiResponse<ApiErrorResponse>> buildErrorResponse(
            HttpStatus status, String message, WebRequest request, List<String> details) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false))
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, errorResponse));
    }
}
