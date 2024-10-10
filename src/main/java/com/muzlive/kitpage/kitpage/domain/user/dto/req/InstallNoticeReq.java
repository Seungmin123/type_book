package com.muzlive.kitpage.kitpage.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstallNoticeReq {

	@NotNull
	@JsonProperty("deviceId")
	private String deviceId;

	@NotNull
	@JsonProperty("serialNumber")
	private String serialNumber;

}
