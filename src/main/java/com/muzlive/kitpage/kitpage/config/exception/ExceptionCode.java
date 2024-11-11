package com.muzlive.kitpage.kitpage.config.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
    USER_NOT_FOUND("10401", "user not found", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_SIGNATURE("10401", "invalid jwt signature.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT("10401", "expired jwt token.", HttpStatus.UNAUTHORIZED),
    NOT_SUPPORTED_JWT("10401", "JWT token not supported.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT("10401", "JWT token is invalid", HttpStatus.UNAUTHORIZED),
    METHOD_NOT_ALLOWED("10400", "http method not allowed", HttpStatus.METHOD_NOT_ALLOWED),

    INVALID_REQUEST_PRAMETER("10400", "invalid request parameter", HttpStatus.BAD_REQUEST),
    CANNOT_FIND_ITEM_THAT_MATCH_THE_PARAM("10400", "There are no items that match the parameters.", HttpStatus.BAD_REQUEST),

    DIFFERENT_APP_KIT("10403", "The kit is not available in kit-page", HttpStatus.FORBIDDEN),
    CANNOT_FIND_MATCHED_ITEM("10404", "There are no items that mate the parameters", HttpStatus.GONE),
    CANNOT_FIND_MATCHED_KIHNO_ITEM("10403", "There are no items in kihno that match the parameters.", HttpStatus.FORBIDDEN),

    NON_DOWNLOADABLE_TOKEN("10405", "Downloads are not possible with this token", HttpStatus.FORBIDDEN),

    INTERNAL_SERVER_ERROR("10500", "server error", HttpStatus.INTERNAL_SERVER_ERROR),
    KITTOR_EXTERNAL_SERVER_ERROR("10501", "kittor external server error", HttpStatus.INTERNAL_SERVER_ERROR),

    YOUTUBE_UPLOAD_ERROR("10601", "youtube upload error")
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
