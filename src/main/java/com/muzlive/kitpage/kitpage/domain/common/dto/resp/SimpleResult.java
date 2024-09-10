package com.muzlive.kitpage.kitpage.domain.common.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleResult {

    protected String code = "200";

    protected String message = "";

    public SimpleResult(String message) {
        this.message = message;
    }

}
