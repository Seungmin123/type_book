package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateKitReq {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("serialNumber")
	private String serialNumber;
}
