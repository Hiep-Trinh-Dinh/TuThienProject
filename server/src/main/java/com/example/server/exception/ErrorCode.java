package com.example.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_EXISTED(1001,"user existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002,"user not found",HttpStatus.NOT_FOUND),
    PASSWORD_INVALID(1003,"Password must be at least {min} characters", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1004,"unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005,"You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_KEY(1006,"Uncategorized",HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND(1007,"permission not found",HttpStatus.NOT_FOUND),
    PHONE_INVALID(1008,"Phone number is invalid", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_INCORRECT(1003,"Password is not correct", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
