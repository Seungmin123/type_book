package com.muzlive.kitpage.kitpage.domain.common.dto.resp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Result<K> extends SimpleResult {

    private K data;

    public Result(K data) {
        this.data = data;
    }

    public Result(String message, K data) {
        this.message = message;
        this.data = data;
    }

    public Result(int status, String message, K data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
