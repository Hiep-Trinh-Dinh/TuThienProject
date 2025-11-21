package com.example.server.exception;

import com.example.server.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> HandlingException(Exception exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(1001);
        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> HandlingException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> HandlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }


    @ExceptionHandler(value = ParseException.class)
    ResponseEntity<ApiResponse> HandlingParseException(ParseException exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(1005);
        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> HandlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        // Ưu tiên lấy fieldError trước, nếu null thì lấy globalError
        String enumKey = null;
        if (exception.getFieldError() != null) {
            enumKey = exception.getFieldError().getDefaultMessage();
        } else if (!exception.getGlobalErrors().isEmpty()) {
            enumKey = exception.getGlobalErrors().get(0).getDefaultMessage();
        }

        // Nếu enumKey vẫn null (trường hợp hiếm), fallback
        if (enumKey == null) {
            enumKey = "INVALID_KEY";
        }

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(enumKey);

            // unwrap constraint nếu có
            var constraintViolation = exception.getBindingResult()
                    .getAllErrors().get(0).unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
        } catch (IllegalArgumentException | NullPointerException e) {
            // ignore: dùng mặc định INVALID_KEY
        }

        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(errorCode.getCode())
                .message(Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage())
                .build());
    }


    private String mapAttribute(String msg, Map<String, Object> attributes){
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return msg.replace("{"+MIN_ATTRIBUTE+"}",minValue);
    }
}
