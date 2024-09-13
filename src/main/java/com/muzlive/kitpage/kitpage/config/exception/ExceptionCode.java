package com.muzlive.kitpage.kitpage.config.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
    USER_NOT_FOUND("401", "user not found", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_SIGNATURE("401", "invalid jwt signature.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT("401", "expired jwt token.", HttpStatus.UNAUTHORIZED),
    NOT_SUPPORTED_JWT("401", "JWT token not supported.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT("401", "JWT token is invalid", HttpStatus.UNAUTHORIZED),
    METHOD_NOT_ALLOWED("400", "http method not allowed", HttpStatus.METHOD_NOT_ALLOWED),

    CANNOT_FIND_MATCHED_ITEM(HttpStatus.UNPROCESSABLE_ENTITY),

    INTERNAL_SERVER_ERROR("500", "server error", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    // add error code -> update

    private String code;

    private String message;

    private HttpStatus status;


    ExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
        this.status = HttpStatus.OK;
    }

    ExceptionCode(HttpStatus status) {
        this.code = String.valueOf(status.value());
        this.message = status.getReasonPhrase();
        this.status = status;
    }

    ExceptionCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public static ExceptionCode getItemByCode(String code) {
        for (ExceptionCode item : ExceptionCode.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
