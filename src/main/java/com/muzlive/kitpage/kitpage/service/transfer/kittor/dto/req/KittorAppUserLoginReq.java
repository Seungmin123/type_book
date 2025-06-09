package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorAppUserLoginReq {

	@NotNull
	@JsonProperty("email")
	private String email;

	@NotNull
	@JsonProperty("password")
	private String password;

	@Hidden
	@JsonProperty("deviceId")
	private String deviceId;

	@Hidden
	@JsonProperty("refreshToken")
	private String refreshToken;

}
