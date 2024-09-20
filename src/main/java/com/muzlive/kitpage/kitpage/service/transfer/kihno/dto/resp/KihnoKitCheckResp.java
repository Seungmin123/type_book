package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KihnoKitCheckResp {

    @JsonProperty("token")
    private String token;

    @JsonProperty("appID")
    private String albumId;

    @JsonProperty("serialnum")
    private String serialNum;

    @JsonProperty("kit_unique_id")
    private Long kihnoKitUid;

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("errorMSG")
    private String errorMsg;

    @JsonProperty("service_type")
    private String serviceType;

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("package_name_and")
    private String packageNameAnd;

    @JsonProperty("package_name_ios")
    private String packageNameIos;

    @JsonProperty("market_id")
    private String marketId;


}
