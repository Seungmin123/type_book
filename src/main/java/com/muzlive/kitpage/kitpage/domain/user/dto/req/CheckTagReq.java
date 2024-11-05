package com.muzlive.kitpage.kitpage.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckTagReq {

	@NotNull
	@JsonProperty("deviceId")
	private String deviceId;

	@NotNull
	@JsonProperty("serialNumber")
	private String serialNumber;

	@NotNull
	@JsonProperty("region")
	private String region;
}
