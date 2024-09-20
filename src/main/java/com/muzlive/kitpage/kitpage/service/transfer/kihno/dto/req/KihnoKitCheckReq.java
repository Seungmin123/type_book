package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KihnoKitCheckReq {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("kit_id")
    private String kitId;

    @Builder.Default
    @JsonProperty("country_code")
    private String countryCode = ApplicationConstants.KOR_COUNTRY_CODE;

}
