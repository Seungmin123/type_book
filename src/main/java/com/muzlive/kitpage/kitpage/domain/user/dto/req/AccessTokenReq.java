package com.muzlive.kitpage.kitpage.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenReq {

	@JsonProperty("deviceId")
	private String deviceId;

	@JsonProperty("region")
	private String region;

	@JsonProperty("modelName")
	private String modelName;

}
