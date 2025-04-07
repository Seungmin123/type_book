package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorOAuthLoginReq {

	@NotNull
	@JsonProperty("idToken")
	private String idToken;

	@Hidden
	@JsonProperty("device")
	private String device;

}
