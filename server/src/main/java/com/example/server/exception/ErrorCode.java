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
    INVALID_EMAIL(1013,"Invalid email format", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(1008,"Phone number must start with 0 and be 10–11 digits", HttpStatus.INTERNAL_SERVER_ERROR),
    PERMISSION_NOT_FOUND(1007,"permission not found",HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(1009,"Password is not correct", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_EMPTY(1010,"Email cannot be empty", HttpStatus.BAD_REQUEST),
    PASSWORD_EMPTY(1011,"Password cannot be empty", HttpStatus.BAD_REQUEST),
    FULL_NAME_EMPTY(1012,"Full name cannot be empty",HttpStatus.BAD_REQUEST),
    INCORRECT_LOGIN(1014,"Email or password is not correct. Please check again.", HttpStatus.NOT_FOUND),
    CURRENT_PASSWORD_CANNOT_MATCH_NEW_PASSWORD(1015,"Your new password cannot be as same as your former password!",HttpStatus.BAD_REQUEST),
    EXPIRED_OTP(1016,"Otp has expired!",HttpStatus.EXPECTATION_FAILED),
    TOKEN_NOT_FOUND(1017,"Token not found",HttpStatus.NOT_FOUND),
    INVALID_PARAM(1009, "Invalid parameter", HttpStatus.BAD_REQUEST),
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
