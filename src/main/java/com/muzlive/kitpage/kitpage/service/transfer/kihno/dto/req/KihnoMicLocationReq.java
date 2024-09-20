package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.MicLocationReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KihnoMicLocationReq {

    @JsonProperty("model_name")
    private String modelName;

    @JsonProperty("device_id")
    private String deviceId;

    public KihnoMicLocationReq(MicLocationReq micLocationReq){
        this.modelName = micLocationReq.getModelName();
        this.deviceId = micLocationReq.getDeviceId();
    }
}
