package com.muzlive.kitpage.kitpage.domain.common.dto.resp;

import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import software.amazon.awssdk.http.HttpStatusCode;


public class CommonResp<T> extends ResponseEntity<Result<T>> {

    public CommonResp() {
        super(new Result<>(String.valueOf(HttpStatusCode.OK), ApplicationConstants.SUCCESS, null), HttpStatus.OK);
    }

    public CommonResp(T body) {
        super(new Result<>(String.valueOf(HttpStatusCode.OK), ApplicationConstants.SUCCESS, body), HttpStatus.OK);
    }

    public CommonResp(HttpStatus status, @Nullable T body) {
        super(new Result<>(String.valueOf(status.value()), null, body), status);
    }

    public CommonResp(String message, @Nullable T body) {
        super(new Result<>(String.valueOf(HttpStatusCode.OK), message, body), HttpStatus.OK);
    }

    public CommonResp(HttpStatus status, String message, @Nullable T body) {
        super(new Result<>(String.valueOf(status.value()), message, body), status);
    }

}
