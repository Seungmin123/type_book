package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateKitReq {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("serialNumber")
	private String serialNumber;
}
