package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KihnoMicProcessedResp {

    @JsonProperty("_result")
    private boolean result;

    @JsonProperty("result_code")
    private String mikePlace;

    @JsonProperty("errorMSG")
    private String errorMsg;

}
