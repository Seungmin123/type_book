package com.muzlive.kitpage.kitpage.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MicLocationReq {

    @NotNull
    @JsonProperty("modelName")
    private String modelName;

    @NotNull
    @JsonProperty("deviceId")
    private String deviceId;
}
